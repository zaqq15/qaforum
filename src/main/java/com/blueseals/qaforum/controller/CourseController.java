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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import com.blueseals.qaforum.dto.*;

import javax.xml.transform.sax.SAXResult;
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
        boolean isTitular = course.getProfessor().getEmail().equals(userDetails.getUsername());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean canManage = isTitular || isAdmin;

        model.addAttribute("course", course);
        model.addAttribute("threads", threadService.getThreadsForCourse(id));
        model.addAttribute("currentUser", userDetails.getUsername());
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("canManage", canManage);

        return "course_view";
    }

    // create a new thread
    @PostMapping("/{id}/thread")
    public String createThread(@PathVariable Long id,
                               @Valid @ModelAttribute ThreadCreateRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/courses/" + id;
        }

        User author = userService.findByEmail(userDetails.getUsername());
        threadService.createThread(id, request.getTitle(), request.getContent(), author, request.getFile());

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
    public String createCourse(@Valid @ModelAttribute CourseCreateRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal UserDetails userDetails) {
                               
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/dashboard";
        }
        
        User professor = userService.findByEmail(userDetails.getUsername());

        if (professor.getRole() == Role.STUDENT) {
            throw new IllegalStateException("Only professors can create courses!");
        }

        courseService.createCourse(request.getTitle(), request.getCourseCode(), request.getDescription(), professor.getId());
        return "redirect:/dashboard";
    }

    @PostMapping("{id}/delete")
    public String deleteCourse(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        courseService.deleteCourse(id, user);
        return "redirect:/dashboard";
    }

    @GetMapping("{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        User user = userService.findByEmail(userDetails.getUsername());
        boolean isTitular = course.getProfessor().getEmail().equals(userDetails.getUsername());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isTitular && !isAdmin) {
            return "redirect:/courses/" + id; // unauthorized
        }
        model.addAttribute("course", course);
        return "edit_course";

    }

    @PostMapping("{id}/update")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute CourseCreateRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal UserDetails userDetails) {
                               
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/courses/" + id + "/edit";
        }
        
        User user = userService.findByEmail(userDetails.getUsername());

        courseService.updateCourse(id, request.getTitle(), request.getDescription(), request.getCourseCode(), user);
        return "redirect:/courses/" + id;
    }


}
