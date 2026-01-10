package com.blueseals.qaforum.repository;

import com.blueseals.qaforum.model.Post;
import com.blueseals.qaforum.model.Vote;
import com.blueseals.qaforum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface VoteRepository extends JpaRepository<Vote, Long>{
    Optional<Vote> findByUserAndPost(User user, Post post);


}
