package com.library.project.dto;

import com.library.project.entity.User;

public class UserResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private User.Role role;

    public UserResponseDTO(Long userId, String name, String email, User.Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public User.Role getRole() {
        return role;
    }
}
