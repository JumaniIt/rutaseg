package com.jumani.rutaseg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponse {
    private final long id;
    private String nickname;
    private String email;
    private boolean admin;
}