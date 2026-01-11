package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ThreadService threadService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);

        model.addAttribute("myThreads", threadService.getThreadsByUser(user));
        model.addAttribute("myPosts", threadService.getPostsByUser(user));

        return "profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername());

        if (!userService.verifyPassword(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
        }

        userService.updatePassword(user, newPassword);

        redirectAttributes.addFlashAttribute("success", "Password updated successfully");
        return "redirect:/profile";
    }
}
