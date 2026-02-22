package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.model.Course;
import com.blueseals.qaforum.model.ForumThread;
import com.blueseals.qaforum.service.CourseService;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByEmail(userDetails.getUsername());

        List<Course> allCourses = courseService.getAllCourses();
        List<Course> myCourses;

        if (currentUser.getRole() == Role.STUDENT) {
            myCourses = courseService.getCoursesForStudent(currentUser.getId());
        } else if (currentUser.getRole() == Role.PROFESSOR) {
            myCourses = courseService.getCoursesForProfessor(currentUser.getId());

        }
        else if (currentUser.getRole() == Role.ADMIN) {
            myCourses = courseService.getAllCourses();
        } else {
            myCourses = new ArrayList<>();
        }


        // calculate "available courses" for student
        List<Course> availableCourses = allCourses.stream()
                        .filter(c -> !myCourses.contains(c))
                        .collect(Collectors.toList());

        model.addAttribute("username", userDetails.getUsername());
        String firstName = currentUser.getFullName() != null ? currentUser.getFullName().split(" ")[0] : "User";
        model.addAttribute("firstName", firstName);
        model.addAttribute("myCourses", myCourses);
        model.addAttribute("availableCourses", availableCourses);

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";

    }
}
