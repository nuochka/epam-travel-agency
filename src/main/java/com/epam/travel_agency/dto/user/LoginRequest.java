package com.epam.travel_agency.dto.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
