package com.blueseals.qaforum.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.Thread.sleep;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String username, String message){
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }

    public void sendGlobalNotification(String message){
        messagingTemplate.convertAndSend("/topic/global", message);
    }

    @Async
    public void broadcastSystemAnnouncement(String message){
        try {
            System.out.println("Sending [" + Thread.currentThread().getName() + "] started broadcast...");
            sleep(3000);

            messagingTemplate.convertAndSend("/topic/announcements", "SYSTEM: " + message);
            System.out.println("Sending [" + Thread.currentThread().getName() + "] finished broadcast...");
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
