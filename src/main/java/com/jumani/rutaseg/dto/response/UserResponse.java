package com.jumani.rutaseg.dto.response;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class UserResponse {
    private final long id;
    private String name;
    private String email;
    private boolean admin;
}