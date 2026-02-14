package com.example.estrera.dto;

public class UserResponse {
    private Long user_id;
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean enabled;

    public UserResponse() {}

    public UserResponse(Long user_id, String username, String email,
                        Boolean isActive, Boolean enabled) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
        this.enabled = enabled;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long user_id;
        private String username;
        private String email;
        private Boolean isActive;
        private Boolean enabled;

        public Builder user_id(Long user_id) {
            this.user_id = user_id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(user_id, username, email, isActive, enabled);
        }
    }
}