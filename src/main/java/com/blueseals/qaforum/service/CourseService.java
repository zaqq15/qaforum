package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.Course;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course createCourse(String title, String code, String description, Long professorId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        Course course = new Course();
        course.setTitle(title);
        course.setCourseCode(code);
        course.setDescription(description);
        course.setProfessor(professor);

        return courseRepository.save(course);
    }

    public void enrollStudent(Long courseId, String studentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        course.getStudents().add(student);
        courseRepository.save(course);
    }
}
