package com.etube.messaging;

import com.etube.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishVideoUploaded(Long videoId, String title, String filePath) {
        VideoUploadedEvent event = new VideoUploadedEvent(videoId, title, filePath);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.UPLOAD_ROUTING_KEY,
                event
        );
    }

    public void publishVideoViewed(Long videoId, String ipAddress) {
        VideoViewedEvent event = new VideoViewedEvent(videoId, ipAddress);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.VIEW_ROUTING_KEY,
                event
        );
    }

    public static class VideoUploadedEvent implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long videoId;
        private String title;
        private String filePath;

        public VideoUploadedEvent() {
        }

        public VideoUploadedEvent(Long videoId, String title, String filePath) {
            this.videoId = videoId;
            this.title = title;
            this.filePath = filePath;
        }

        public Long getVideoId() {
            return videoId;
        }

        public void setVideoId(Long videoId) {
            this.videoId = videoId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    public static class VideoViewedEvent implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long videoId;
        private String ipAddress;

        public VideoViewedEvent() {
        }

        public VideoViewedEvent(Long videoId, String ipAddress) {
            this.videoId = videoId;
            this.ipAddress = ipAddress;
        }

        public Long getVideoId() {
            return videoId;
        }

        public void setVideoId(Long videoId) {
            this.videoId = videoId;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }
}
