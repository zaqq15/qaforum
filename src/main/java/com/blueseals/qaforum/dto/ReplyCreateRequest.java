package com.blueseals.qaforum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReplyCreateRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private MultipartFile file;
}
