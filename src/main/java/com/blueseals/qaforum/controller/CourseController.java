package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.Course;
import com.blueseals.qaforum.model.ForumThread;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.CourseRepository;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    // view a specific course and its threads
    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        model.addAttribute("course", course);
        model.addAttribute("threads", threadService.getThreadsForCourse(id));
        model.addAttribute("currentUser", userDetails.getUsername());

        return "course_view";
    }

    // create a new thread
    @PostMapping("/{id}/thread")
    public String createThread(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String content,
                               @AuthenticationPrincipal UserDetails userDetails) {

        User author = userService.findByEmail(userDetails.getUsername());
        threadService.createThread(id, title, content, author);

        return "redirect:/courses/" + id;
    }
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
}
