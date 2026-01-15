package com.blueseals.qaforum;

import com.blueseals.qaforum.model.*;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.repository.ForumThreadRepository;
import com.blueseals.qaforum.repository.PostRepository;
import com.blueseals.qaforum.service.ThreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ThreadServiceTest {

    @Mock
    private ForumThreadRepository threadRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ThreadService threadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createThread_ShouldSaveThreadAndFirstPost() throws IOException {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        User author = new User();
        author.setId(10L);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(threadRepository.save(any(ForumThread.class))).thenAnswer(i -> i.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ForumThread result = threadService.createThread(1L, "Title", "Body", author, file);

        // Assert
        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldDeletePost_WhenUserIsAuthor() {
        // Arrange
        Post post = new Post();
        post.setId(5L);
        User author = new User();
        author.setId(10L);
        post.setUser(author);

        // Mock relationships
        ForumThread thread = new ForumThread();
        Course course = new Course();
        course.setProfessor(new User());
        thread.setCourse(course);
        post.setThread(thread);
        thread.getPosts().add(post);

        when(postRepository.findById(5L)).thenReturn(Optional.of(post));

        // Act
        threadService.deletePost(5L, author);

        // Assert
        assertTrue(post.isDeleted());
        assertEquals("[THIS REPLY HAS BEEN DELETED]", post.getContentText());
        verify(postRepository, times(1)).save(post);
    }
}
