package com.jumani.rutaseg.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRequest {

    @NotNull
    private String nickname;
    @Email
    @NotNull
    private String email;
    @NotNull
    private String password;
    private boolean admin;
}
