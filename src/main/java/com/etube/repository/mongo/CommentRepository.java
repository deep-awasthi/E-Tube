package com.etube.repository.mongo;

import com.etube.model.mongo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByVideoIdOrderByCreatedAtDesc(Long videoId);
}
