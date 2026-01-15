package com.blueseals.qaforum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.blueseals.qaforum.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('PROFESSOR')")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/announcements")
    public String announcementsPage(){
        return "admin_announcements";
    }

    @PostMapping(value = "/broadcast", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> sendBroadcast(@RequestParam String message) {
        try {

        notificationService.broadcastSystemAnnouncement(message);
        return ResponseEntity.ok("Announcement sent to all users successfully!");
    }  catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending announcement: " + e.getMessage());
        }
    }


}
