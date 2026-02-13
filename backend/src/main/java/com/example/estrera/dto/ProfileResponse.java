package com.example.estrera.dto;

public class ProfileResponse {
    private String username;
    private Boolean enabled;
    private String email;

    public ProfileResponse() {
    }

    public ProfileResponse(String username, Boolean enabled, String email) {
        this.username = username;
        this.enabled = enabled;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}