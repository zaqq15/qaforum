package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.*;
import com.blueseals.qaforum.repository.ForumThreadRepository;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThreadService {

    @Autowired
    private ForumThreadRepository threadRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PostRepository postRepository;

    public List<ForumThread> getThreadsForCourse(Long courseId) {
        return threadRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    public ForumThread getThreadById(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));
    }

    public Post getPostById(Long PostId) {
        return postRepository.findById(PostId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }


    /**
     * context-aware search
     * ranks threads based on keyword relevance in title and description
     */
    public List<ForumThread> searchThreadsinCourse(Long courseId, String query) {
        if(query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        String[] keywords = query.toLowerCase().split("\\s+");

        // fetch broad candidates from DB (anything matching at least one part)
        List<ForumThread> candidates =  threadRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        // if query has multiple words, fetch all for maximum accuracy in ranking
        if(candidates.isEmpty() || keywords.length > 1) {
            candidates = threadRepository.findAll();
        }

        // score and rank
        Map<ForumThread, Integer> scores = new HashMap<>();

        for (ForumThread thread : candidates) {
            int score = 0;
            String title = thread.getTitle().toLowerCase();
            String description = thread.getDescription() != null ? thread.getDescription().toLowerCase() : "";

            for (String word : keywords) {
                // exact word match in title is heavily weighted
                if(title.contains(word)) {
                    score += 10;
                }
                // match in description is weighted less
                if(description.contains(word)) {
                    score += 3;
                }
            }
            if(score > 0) {
                scores.put(thread, score);
            }
        }

        // sort by score and return list
        return scores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Transactional
    public ForumThread createThread(Long courseId, String title, String content, User author, MultipartFile file) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // create the thread
        ForumThread thread = new ForumThread();
        thread.setTitle(title);
        thread.setCourse(course);
        thread.setDescription(content);
        thread.setCreatedAt(LocalDateTime.now());

        ForumThread savedThread = threadRepository.save(thread);

        // create the first post (question body)
        Post firstPost = new Post();
        firstPost.setContentText(content);
        firstPost.setThread(savedThread);
        firstPost.setCreatedAt(LocalDateTime.now());

        // handle file
        if (file != null && !file.isEmpty()) {
            firstPost.setFileName(file.getOriginalFilename());
            firstPost.setFileType(file.getContentType());
            firstPost.setFileData(file.getBytes());
        }

        // anonymity logic: user is linked => view handles hiding student info
        firstPost.setUser(author);


        postRepository.save(firstPost);
        return savedThread;
    }

    @Transactional
    public Post addReply(Long threadId, String content, User author, MultipartFile file) throws IOException {
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        Post reply = new Post();
        reply.setContentText(content);
        reply.setThread(thread);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(author);

        // handle file
        if (file != null && !file.isEmpty()) {
            reply.setFileName(file.getOriginalFilename());
            reply.setFileType(file.getContentType());
            reply.setFileData(file.getBytes());
        }

        return postRepository.save(reply);
    }

    @Transactional
    public void markAsAccepted(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        ForumThread thread = post.getThread();

        // only the thread creator or a professor/admin can mark a post as accepted
        Post originalPost = thread.getPosts().get(0);
        User threadAuthor = originalPost.getUser();

        boolean isThreadAuthor = threadAuthor != null && threadAuthor.getId().equals(currentUser.getId());
        boolean isProfessorOrAdmin = currentUser.getRole().equals(Role.PROFESSOR) || currentUser.getRole().equals(Role.ADMIN);

        if(!isThreadAuthor && !isProfessorOrAdmin) {
            throw new RuntimeException("Only the thread creator or a professor/admin can mark a post as accepted");
        }

        for (Post p : thread.getPosts()) {
            p.setAccepted(false);
        }

        post.setAccepted(true);
        postRepository.saveAll(thread.getPosts());
    }

    // delete thread (hard delete)
    @Transactional
    public void deleteThread(Long threadId, User requester) {
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        boolean isAuthor = thread.getPosts().get(0).getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        boolean isTitular = thread.getCourse().getProfessor().getId().equals(requester.getId());

        if(!isAuthor && !isAdmin && !isTitular) {
            throw new RuntimeException("Unauthorized to delete thread.");
        }

        threadRepository.delete(thread);
    }

    // delete post (soft delete for replies, hard delete for questions)
    @Transactional
    public void deletePost(Long postId, User requester) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isAuthor = post.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        boolean isTitular = post.getThread().getCourse().getProfessor().getId().equals(requester.getId());

        if(!isAuthor && !isAdmin && !isTitular) {
            throw new RuntimeException("Unauthorized to delete post.");
        }

        if (post.getThread().getPosts().indexOf(post) == 0) {
            threadRepository.delete(post.getThread());
        } else {
            post.setDeleted(true);
            post.setContentText("[THIS REPLY HAS BEEN DELETED]");
            post.setFileName(null);
            post.setFileData(null);
            postRepository.save(post);

        }
    }

}


