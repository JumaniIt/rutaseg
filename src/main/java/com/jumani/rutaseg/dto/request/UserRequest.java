package com.jumani.rutaseg.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserRequest {
    private String nickname;
    private String email;
    private String password;
    private boolean admin;
}
