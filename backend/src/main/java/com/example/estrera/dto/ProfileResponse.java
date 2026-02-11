package com.example.estrera.dto;

public class ProfileResponse {
    private String username;
    private Boolean enabled;

    public ProfileResponse() {
    }

    public ProfileResponse(String username, Boolean enabled) {
        this.username = username;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}