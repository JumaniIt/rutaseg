package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@Getter
@Entity
@FieldNameConstants
@Table(name = "containers")
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "code")
    private String code;

    @Column(name = "measures")
    @Enumerated(EnumType.STRING)
    private Measures measures;

    @Column(name = "repackage")
    private boolean repackage;

    @Column(name = "pema")
    private String pema;


    public Container(String code, Measures measures, boolean repackage, String pema) {
    }
}
