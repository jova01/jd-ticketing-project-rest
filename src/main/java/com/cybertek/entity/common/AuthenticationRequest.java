package com.cybertek.entity.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;
}
