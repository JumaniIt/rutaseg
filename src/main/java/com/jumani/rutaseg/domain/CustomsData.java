package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "customs_datas")
@Slf4j
public class CustomsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;


    @Column(name = "cuit")
    private Long cuit;

    private CustomsData() {
    }

    public CustomsData(String name, String phone, Long cuit) {
        this.name = name;
        this.phone = phone;
        this.cuit = cuit;
    }
}
