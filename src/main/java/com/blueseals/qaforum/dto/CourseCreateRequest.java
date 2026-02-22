package com.blueseals.qaforum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCreateRequest {

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Course code is required")
    @Size(min = 1, max = 10, message = "Course code must be between 1 and 10 characters")
    private String courseCode;

    @NotBlank(message = "Course description is required")
    private String description;
}
