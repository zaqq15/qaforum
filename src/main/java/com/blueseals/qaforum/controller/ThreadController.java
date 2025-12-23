package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.User;
import org.springframework.stereotype.Controller;
import com.blueseals.qaforum.model.ForumThread;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ThreadController {

    @Autowired
    private ThreadService threadService;

    @Autowired
    private UserService userService;

    // view a specific thread and its posts
    @GetMapping("/thread/{id}")
    public String viewThread(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        ForumThread thread = threadService.getThreadById(id);

        model.addAttribute("thread", thread);
        model.addAttribute("username", userDetails.getUsername());

        return "thread_view";
    }

    // handle reply submission
    @PostMapping("/thread/{id}/reply")
    public String postReply(@PathVariable Long id, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails) {

        User author = userService.findByEmail(userDetails.getUsername());
        threadService.addReply(id, content, author);

        return "redirect:/thread/" + id;
    }

}
