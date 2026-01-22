package com.blueseals.qaforum;

import com.blueseals.qaforum.model.Course;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.repository.UserRepository;
import com.blueseals.qaforum.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCourse_Success() {
        User professor = new User();
        professor.setId(1L);
        professor.setRole(Role.PROFESSOR);

        when(userRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Course created = courseService.createCourse("Intro to Java", "CS101", "Basics", 1L);

        assertNotNull(created);
        assertEquals("Intro to Java", created.getTitle());
        assertEquals("CS101", created.getCourseCode());
        assertEquals(professor, created.getProfessor());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void enrollStudent_Success() {
        Course course = new Course();
        course.setId(10L);

        User student = new User();
        student.setEmail("student@test.com");

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));

        courseService.enrollStudent(10L, "student@test.com");

        assertTrue(course.getStudents().contains(student));
        verify(courseRepository, times(1)).save(course);
    }
}