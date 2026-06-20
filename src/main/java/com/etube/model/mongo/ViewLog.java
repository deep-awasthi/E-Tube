package com.etube.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "view_logs")
public class ViewLog {
    
    @Id
    private String id;
    
    private Long videoId;
    private String ipAddress;
    private Date timestamp;

    public ViewLog() {
    }

    public ViewLog(String id, Long videoId, String ipAddress, Date timestamp) {
        this.id = id;
        this.videoId = videoId;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public static ViewLogBuilder builder() {
        return new ViewLogBuilder();
    }

    public static class ViewLogBuilder {
        private String id;
        private Long videoId;
        private String ipAddress;
        private Date timestamp;

        public ViewLogBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ViewLogBuilder videoId(Long videoId) {
            this.videoId = videoId;
            return this;
        }

        public ViewLogBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public ViewLogBuilder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ViewLog build() {
            return new ViewLog(id, videoId, ipAddress, timestamp);
        }
    }
}
