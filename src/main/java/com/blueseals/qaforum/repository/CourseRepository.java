package com.blueseals.qaforum.repository;

import com.blueseals.qaforum.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // find a course by its unique code
    Course findByCourseCode(String courseCode);

    List<Course> findByProfessorId(Long professorId);
}
