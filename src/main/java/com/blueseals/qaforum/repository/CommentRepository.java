package com.blueseals.qaforum.repository;

import com.blueseals.qaforum.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{
}
