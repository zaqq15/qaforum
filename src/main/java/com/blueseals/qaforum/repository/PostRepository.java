package com.blueseals.qaforum.repository;

import com.blueseals.qaforum.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // get all posts for a specific thread, newest -> oldest
    List<Post> findByThreadIdOrderByCreatedAtDesc(Long threadId);
}
