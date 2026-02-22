package com.blueseals.qaforum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid e-uvt email format")
    @Pattern(regexp = "^[A-Za-z]+\\.[A-Za-z]+(\\d{2})?@e-uvt\\.ro$", message = "Email must be a valid e-uvt email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
