package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "customs_data")
@Slf4j
public class CustomsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;


    @Column(name = "CUIT")
    private Long CUIT;

    private CustomsData() {
    }

    public CustomsData(String name, String phone, Long CUIT) {
        this.name = name;
        this.phone = phone;
        this.CUIT = CUIT;
    }
}
