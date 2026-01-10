package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.Vote;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.blueseals.qaforum.repository.VoteRepository;
import com .blueseals.qaforum.repository.PostRepository;

import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public void castVote(Long postId, User user, Vote.VoteType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Optional<Vote> existingVote = voteRepository.findByUserAndPost(user, post);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getType() == type) {
                // if a user already voted for the same type, remove the vote
                voteRepository.delete(vote);
            } else {
                // change vote type
                vote.setType(type);
                voteRepository.save(vote);
            }
        } else {
            // new vote
            Vote vote = new Vote();
            vote.setUser(user);
            vote.setPost(post);
            vote.setType(type);
            voteRepository.save(vote);
        }
    }
}
