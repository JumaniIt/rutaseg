package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.time.ZonedDateTime;
import java.util.Optional;
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
