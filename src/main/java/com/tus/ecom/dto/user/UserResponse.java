package com.tus.ecom.dto.user;

import com.tus.ecom.model.RoleEntity;

public class UserResponse {
    private Integer id;
    private String username;
    private RoleEntity role;

    public UserResponse(Integer id, String username, RoleEntity role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}
