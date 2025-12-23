package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.*;
import com.blueseals.qaforum.repository.ForumThreadRepository;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public ForumThread getPostById(Long threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));
    }

    @Transactional
    public ForumThread createThread(Long courseId, String title, String content, User author) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // create the thread
        ForumThread thread = new ForumThread();
        thread.setTitle(title);
        thread.setCourse(course);
        thread.setDescription(content);
        thread.setCreatedAt(LocalDateTime.now());

        ForumThread savedThread = threadRepository.save(thread);

        // create first post (question body)
        Post firstPost = new Post();
        firstPost.setContentText(content);
        firstPost.setThread(savedThread);
        firstPost.setCreatedAt(LocalDateTime.now());

        // anonimity logic : user is linked => view handles hiding student info
        firstPost.setUser(author);


        postRepository.save(firstPost);
        return savedThread;
    }

    @Transactional
    public Post addReply(Long threadId, String content, User author) {
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Thread not found"));

        Post reply = new Post();
        reply.setContentText(content);
        reply.setThread(thread);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(author);

        return postRepository.save(reply);
    }

}


