package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;


@Getter
@Embeddable
public class Container {


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
