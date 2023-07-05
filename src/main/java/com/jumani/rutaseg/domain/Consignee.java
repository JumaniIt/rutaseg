package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Getter
public class Consignee {
    private String name;
    private long CUIT;

    public Consignee(String name, long CUIT) {
        this.name = name;
        this.CUIT = CUIT;
    }

    public Consignee(){
    }
}

