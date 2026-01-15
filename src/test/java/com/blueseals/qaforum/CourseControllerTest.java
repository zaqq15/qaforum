package com.blueseals.qaforum;

import com.blueseals.qaforum.controller.CourseController;
import com.blueseals.qaforum.model.Course;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.service.CourseService;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserService userService;
    @Mock
    private ThreadService threadService;
    @Mock
    private CourseService courseService;
    @Mock
    private Model model;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Constructor Test ---

    @Test
    void testControllerInitialization() {
        // Requirement: Test Constructor/Bean creation logic
        assertNotNull(courseController);
    }

    // --- Functionality Tests (At least 3) ---

    @Test
    void viewCourse_Success() {
        // Functionality 1: View Course Logic
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        User prof = new User();
        prof.setEmail("prof@test.com");
        course.setProfessor(prof);

        User user = new User();
        user.setEmail("student@test.com");
        user.setRole(Role.STUDENT);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userDetails.getUsername()).thenReturn("student@test.com");
        when(userService.findByEmail("student@test.com")).thenReturn(user);

        // Act
        String viewName = courseController.viewCourse(courseId, userDetails, model);

        // Assert
        assertEquals("course_view", viewName);
        verify(model).addAttribute(eq("course"), eq(course));
        verify(model).addAttribute(eq("currentUser"), eq("student@test.com"));
    }

    @Test
    void createCourse_Success_AsProfessor() {
        // Functionality 2: Create Course Logic (Success)
        User prof = new User();
        prof.setRole(Role.PROFESSOR);
        prof.setId(10L);

        when(userDetails.getUsername()).thenReturn("prof@test.com");
        when(userService.findByEmail("prof@test.com")).thenReturn(prof);

        // Act
        String viewName = courseController.createCourse("Title", "Code", "Desc", userDetails);

        // Assert
        assertEquals("redirect:/dashboard", viewName);
        verify(courseService).createCourse("Title", "Code", "Desc", 10L);
    }

    @Test
    void enrollInCourse_Success() {
        // Functionality 3: Enroll Logic
        Long courseId = 1L;
        when(userDetails.getUsername()).thenReturn("student@test.com");

        // Act
        String viewName = courseController.enrollInCourse(courseId, userDetails);

        // Assert
        assertEquals("redirect:/courses/1", viewName);
        verify(courseService).enrollStudent(1L, "student@test.com");
    }
}