package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name ="drivers_data")
@Slf4j
public class DriverData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "company")
    private String company;

    private DriverData() {
    }

    public DriverData(String name, String phone, String company) {
        this.name = name;
        this.phone = phone;
        this.company = company;
    }
}
