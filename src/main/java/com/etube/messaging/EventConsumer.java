package com.etube.messaging;

import com.etube.config.RabbitMQConfig;
import com.etube.model.mongo.ViewLog;
import com.etube.model.postgres.Video;
import com.etube.repository.mongo.ViewLogRepository;
import com.etube.repository.postgres.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    private final VideoRepository videoRepository;
    private final ViewLogRepository viewLogRepository;
    private final CacheManager cacheManager;

    @Autowired
    public EventConsumer(VideoRepository videoRepository, 
                         ViewLogRepository viewLogRepository, 
                         CacheManager cacheManager) {
        this.videoRepository = videoRepository;
        this.viewLogRepository = viewLogRepository;
        this.cacheManager = cacheManager;
    }

    @RabbitListener(queues = RabbitMQConfig.UPLOAD_QUEUE)
    public void consumeVideoUpload(EventPublisher.VideoUploadedEvent event) {
        log.info("Received upload event for video id: {}, title: {}", event.getVideoId(), event.getTitle());
        
        try {
            // Simulate video transcoding and thumbnail generation (takes 3s)
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Video processing was interrupted", e);
        }

        Optional<Video> videoOptional = videoRepository.findById(event.getVideoId());
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            video.setStatus("READY");
            videoRepository.save(video);
            
            // Evict cache to reflect changes
            evictVideoCache(video.getId());
            log.info("Video processing completed for id: {}. Status set to READY.", video.getId());
        } else {
            log.warn("Video not found in Postgres: {}", event.getVideoId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.VIEW_QUEUE)
    public void consumeVideoView(EventPublisher.VideoViewedEvent event) {
        log.info("Received view event for video id: {} from IP: {}", event.getVideoId(), event.getIpAddress());

        try {
            // 1. Log view to MongoDB for analytical tracking
            ViewLog viewLog = ViewLog.builder()
                    .videoId(event.getVideoId())
                    .ipAddress(event.getIpAddress())
                    .timestamp(new Date())
                    .build();
            viewLogRepository.save(viewLog);

            // 2. Increment view count in PostgreSQL
            Optional<Video> videoOptional = videoRepository.findById(event.getVideoId());
            if (videoOptional.isPresent()) {
                Video video = videoOptional.get();
                video.setViewCount(video.getViewCount() + 1);
                videoRepository.save(video);
                
                // Evict cache to make sure the next GET returns the updated view count
                evictVideoCache(video.getId());
                log.info("Successfully updated views in PostgreSQL for video id: {}. Current views: {}", video.getId(), video.getViewCount());
            } else {
                log.warn("Video not found in Postgres to update views: {}", event.getVideoId());
            }
        } catch (Exception e) {
            log.error("Error processing video view asynchronously", e);
        }
    }

    private void evictVideoCache(Long videoId) {
        if (cacheManager != null && cacheManager.getCache("videos") != null) {
            cacheManager.getCache("videos").evict(videoId);
            log.debug("Evicted cache for video id: {}", videoId);
        }
    }
}
