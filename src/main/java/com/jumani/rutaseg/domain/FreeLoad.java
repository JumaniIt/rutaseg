package com.jumani.rutaseg.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
public class FreeLoad {
    @Column(name = "patent")
    private String patent;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private FreeLoadType type;

    @Column(name = "weight")
    private String weight;

    @Column(name = "guide")
    private String guide;

    @Column(name = "pema")
    private String pema;

    private FreeLoad() {

    }

    public FreeLoad(String patent, FreeLoadType type, String weight, String guide, String pema) {
        this.patent = patent;
        this.type = type;
        this.weight = weight;
        this.guide = guide;
        this.pema = pema;
    }
}
