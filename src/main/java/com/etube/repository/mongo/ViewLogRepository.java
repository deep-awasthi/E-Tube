package com.etube.repository.mongo;

import com.etube.model.mongo.ViewLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ViewLogRepository extends MongoRepository<ViewLog, String> {
    List<ViewLog> findByVideoId(Long videoId);
}
