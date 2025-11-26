package com.blueseals.qaforum.repository;

import com.blueseals.qaforum.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // find all courses taught by a specific professor
    List<Course> findByProfessor_Id(Long professorId);

    // find a course by its unique code
    Course findByCourseCode(String courseCode);
}
