package com.jumani.rutaseg.domain;

import lombok.Getter;

@Getter
public class DriverData {
    private String name;
    private String phone;
    private String company;

    private DriverData() {
    }

    public DriverData(String name, String phone, String company) {
        this.name = name;
        this.phone = phone;
        this.company = company;
    }
}
