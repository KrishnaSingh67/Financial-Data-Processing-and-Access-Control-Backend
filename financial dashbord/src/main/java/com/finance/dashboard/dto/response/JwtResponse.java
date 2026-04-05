package com.finance.dashboard.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private final String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private String role;

    public JwtResponse(String token, Long id, String name, String email, String role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
