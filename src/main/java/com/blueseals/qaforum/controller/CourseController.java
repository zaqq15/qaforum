package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.Course;
import com.blueseals.qaforum.model.ForumThread;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.service.CourseService;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ThreadService threadService;
    @Autowired
    private CourseService courseService;


    // view a specific course and its threads
    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User user = userService.findByEmail(userDetails.getUsername());

        boolean isEnrolled = course.getStudents().contains(user);

        model.addAttribute("course", course);
        model.addAttribute("threads", threadService.getThreadsForCourse(id));
        model.addAttribute("currentUser", userDetails.getUsername());
        model.addAttribute("isEnrolled", isEnrolled);

        return "course_view";
    }

    // create a new thread
    @PostMapping("/{id}/thread")
    public String createThread(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String content,
                               @RequestParam(required = false) MultipartFile file,
                               @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User author = userService.findByEmail(userDetails.getUsername());
        threadService.createThread(id, title, content, author, file);

        return "redirect:/courses/" + id;
    }

    // search for threads in a course
    @GetMapping("/{id}/search")
    public String searchCourseThreads(@PathVariable Long id,
                                      @RequestParam String query,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<ForumThread> results = threadService.searchThreadsinCourse(id, query);

        model.addAttribute("course", course); // Needed for "Back to Course" links
        model.addAttribute("query", query);
        model.addAttribute("results", results);

        return "search_results";
    }

    // enroll a student in a course
    @PostMapping("/{id}/enroll")
    public String enrollInCourse(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        courseService.enrollStudent(id, userDetails.getUsername());
        return "redirect:/courses/" + id;
    }

    // create a new course (prof/admin only)
    @PostMapping("/create")
    public String createCourse(@RequestParam String title,
                               @RequestParam String courseCode,
                               @RequestParam String description,
                               @AuthenticationPrincipal UserDetails userDetails) {
        User professor = userService.findByEmail(userDetails.getUsername());

        if (professor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Only professors can create courses!");
        }

        courseService.createCourse(title, courseCode, description, professor.getId());
        return "redirect:/dashboard";
    }
}
