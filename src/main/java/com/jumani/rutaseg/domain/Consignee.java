package com.jumani.rutaseg.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Consignee {
    @Column(name = "name")
    private String name;
    @Column(name = "cuit")
    private long CUIT;

    public Consignee(String name, long CUIT) {
        this.name = name;
        this.CUIT = CUIT;
    }

    public Consignee() {
    }
}

