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

public class CustomsData {

    private String name;

    private String phone;

    private Long CUIL;

    private CustomsData() {
    }

    public CustomsData(String name, String phone, Long CUIL) {
        this.name = name;
        this.phone = phone;
        this.CUIL = CUIL;
    }
}
