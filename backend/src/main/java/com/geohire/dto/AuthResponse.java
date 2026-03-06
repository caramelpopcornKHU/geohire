package com.geohire.dto;

import com.geohire.entity.Role;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
