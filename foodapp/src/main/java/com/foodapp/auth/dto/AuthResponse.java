package com.foodapp.auth.dto;

import com.foodapp.user.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String name;
    private String email;
    private Role role;
}
