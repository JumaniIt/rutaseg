package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Getter
public class User {
    private String name;
    private String password;
    private String email;
    private boolean admin;

    public User(String name, String password, String email, boolean admin) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.admin = admin;
    }
    public User() {
    }
}
