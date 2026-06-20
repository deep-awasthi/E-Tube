package com.etube.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "comments")
public class Comment {
    
    @Id
    private String id;
    
    private Long videoId;
    private String author;
    private String content;
    private Date createdAt;

    public Comment() {
    }

    public Comment(String id, Long videoId, String author, String content, Date createdAt) {
        this.id = id;
        this.videoId = videoId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    public static class CommentBuilder {
        private String id;
        private Long videoId;
        private String author;
        private String content;
        private Date createdAt;

        public CommentBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CommentBuilder videoId(Long videoId) {
            this.videoId = videoId;
            return this;
        }

        public CommentBuilder author(String author) {
            this.author = author;
            return this;
        }

        public CommentBuilder content(String content) {
            this.content = content;
            return this;
        }

        public CommentBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Comment build() {
            return new Comment(id, videoId, author, content, createdAt);
        }
    }
}
