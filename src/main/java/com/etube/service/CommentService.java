package com.etube.service;

import com.etube.model.mongo.Comment;
import com.etube.repository.mongo.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment addComment(Long videoId, String author, String content) {
        Comment comment = Comment.builder()
                .videoId(videoId)
                .author(author)
                .content(content)
                .createdAt(new Date())
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Saved comment in MongoDB with id: {} for videoId: {}", savedComment.getId(), videoId);
        return savedComment;
    }

    public List<Comment> getCommentsByVideoId(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId);
    }
}
