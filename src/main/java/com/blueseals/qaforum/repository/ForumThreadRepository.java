package com.blueseals.qaforum.repository;

import com.blueseals.qaforum.model.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {

    // get all threads for a specific course, newest -> oldest
    List<ForumThread> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    List<ForumThread> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);


}
