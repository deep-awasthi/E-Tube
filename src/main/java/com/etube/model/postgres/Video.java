package com.etube.model.postgres;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "videos")
public class Video implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(nullable = false)
    private String status; // PENDING, PROCESSING, READY

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public Video() {
    }

    public Video(Long id, String title, String description, String filePath, Long viewCount, Long likeCount, String status, Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (viewCount == null) viewCount = 0L;
        if (likeCount == null) likeCount = 0L;
        if (status == null) status = "PENDING";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Simple Builder Pattern
    public static VideoBuilder builder() {
        return new VideoBuilder();
    }

    public static class VideoBuilder {
        private Long id;
        private String title;
        private String description;
        private String filePath;
        private Long viewCount;
        private Long likeCount;
        private String status;
        private Date createdAt;

        public VideoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public VideoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public VideoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VideoBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public VideoBuilder viewCount(Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public VideoBuilder likeCount(Long likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public VideoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public VideoBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Video build() {
            return new Video(id, title, description, filePath, viewCount, likeCount, status, createdAt);
        }
    }
}
