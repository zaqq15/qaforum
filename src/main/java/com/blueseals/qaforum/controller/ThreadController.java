package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.Post;
import com.blueseals.qaforum.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.blueseals.qaforum.model.ForumThread;
import com.blueseals.qaforum.service.ThreadService;
import com.blueseals.qaforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

        User threadAuthor = thread.getPosts().isEmpty() ? null : thread.getPosts().get(0).getUser();
        String currentEmail = userDetails.getUsername();
        boolean isAuthor = threadAuthor != null && threadAuthor.getEmail().equals(currentEmail);

        User currentUser = userService.findByEmail(currentEmail);
        boolean canMark = isAuthor || currentUser.getRole().name().equals("PROFESSOR") || currentUser.getRole().name().equals("ADMIN");

        model.addAttribute("canMark", canMark);

        return "thread_view";
    }

    // handle reply submission
    @PostMapping("/thread/{id}/reply")
    public String postReply(@PathVariable Long id,
                            @RequestParam String content,
                            @RequestParam(required = false) MultipartFile file,
                            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User author = userService.findByEmail(userDetails.getUsername());
        threadService.addReply(id, content, author, file);

        return "redirect:/thread/" + id;
    }

    @PostMapping("/thread/{id}/mark/{postId}")
    public String markAccepted(@PathVariable Long id, @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        threadService.markAsAccepted(postId, user);

        return "redirect:/thread/" + id;
    }

    @GetMapping("/download/post/{postId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long postId) {
        Post post = threadService.getPostById(postId);

        if(post.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + post.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(post.getFileType() != null ? post.getFileType() : "application/octet-stream"))
                .body(post.getFileData());
    }

}
