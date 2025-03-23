package com.academiaconnect.auth.authservice.application.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPage {
    private List<UserResponse> users;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}