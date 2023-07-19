package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Consignee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria

    @Column(name = "name")
    private String name;

    @Column(name = "cuit")
    private long cuit;

    public Consignee(String name, long cuit) {
        this.name = name;
        this.cuit = cuit;
    }

    public Consignee() {
    }
}

