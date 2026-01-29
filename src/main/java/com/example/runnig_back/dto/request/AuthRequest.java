package com.example.runnig_back.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
    private String userName;
}
