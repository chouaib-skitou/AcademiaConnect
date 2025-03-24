package com.academiaconnect.auth.authservice.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "usernameOrEmail must not be blank")
    @JsonAlias({"username", "usernameOrEmail"})
    private String usernameOrEmail;

    @NotBlank(message = "password must not be blank")
    private String password;
}
