package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.TestEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private boolean admin;
}