package com.foodapp.user;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
}
