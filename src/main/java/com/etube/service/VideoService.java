package com.etube.service;

import com.etube.model.postgres.Video;
import com.etube.repository.postgres.VideoRepository;
import com.etube.messaging.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class VideoService {

    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    private final VideoRepository videoRepository;
    private final EventPublisher eventPublisher;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    public VideoService(VideoRepository videoRepository, EventPublisher eventPublisher) {
        this.videoRepository = videoRepository;
        this.eventPublisher = eventPublisher;
    }

    public Video uploadVideo(MultipartFile file, String title, String description) throws IOException {
        // Create upload directory if it does not exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate unique filename
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath);

        // Save metadata to PostgreSQL
        Video video = Video.builder()
                .title(title)
                .description(description)
                .filePath(filePath.toAbsolutePath().toString())
                .viewCount(0L)
                .likeCount(0L)
                .status("PENDING")
                .createdAt(new Date())
                .build();

        Video savedVideo = videoRepository.save(video);
        log.info("Saved video metadata in DB with id: {}", savedVideo.getId());

        // Publish event to RabbitMQ for processing (transcoding/thumbnail simulation)
        eventPublisher.publishVideoUploaded(savedVideo.getId(), savedVideo.getTitle(), savedVideo.getFilePath());

        return savedVideo;
    }

    @Cacheable(value = "videos", key = "#id")
    public Video getVideoById(Long id) {
        log.info("Cache miss: Fetching video details from PostgreSQL for id: {}", id);
        return videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found with id: " + id));
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public void recordView(Long id, String ipAddress) {
        // Publish view event asynchronously to RabbitMQ
        eventPublisher.publishVideoViewed(id, ipAddress);
        log.debug("View event published for video: {} from IP: {}", id, ipAddress);
    }

    public ResourceRegion streamVideo(Long id, String rangeHeader) throws IOException {
        Video video = getVideoById(id); // Retrieves (cached or from DB)
        Resource resource = new FileSystemResource(video.getFilePath());
        
        if (!resource.exists()) {
            throw new IOException("Video file not found at path: " + video.getFilePath());
        }

        long contentLength = resource.contentLength();
        long chunkMin = 1024 * 1024; // 1 MB chunk sizes by default

        if (rangeHeader == null || rangeHeader.trim().isEmpty()) {
            long rangeLength = Math.min(chunkMin, contentLength);
            return new ResourceRegion(resource, 0, rangeLength);
        }

        List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
        if (ranges.isEmpty()) {
            long rangeLength = Math.min(chunkMin, contentLength);
            return new ResourceRegion(resource, 0, rangeLength);
        }

        HttpRange range = ranges.get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(chunkMin, end - start + 1);

        return new ResourceRegion(resource, start, rangeLength);
    }
}
