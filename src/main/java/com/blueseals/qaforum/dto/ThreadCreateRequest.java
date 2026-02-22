package com.blueseals.qaforum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ThreadCreateRequest {

    @NotBlank(message = "Topic / Title is required")
    private String title;

    @NotBlank(message = "Details are required")
    private String content;

    private MultipartFile file;
}
