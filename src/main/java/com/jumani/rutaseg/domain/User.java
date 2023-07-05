package com.jumani.rutaseg.domain;

import lombok.Getter;

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

    private User() {
    }
}
