package com.jumani.rutaseg.domain;
import lombok.Getter;

@Getter
public class CustomsData {

    private String name;

    private String phone;

    private Long CUIT;

    private CustomsData() {
    }

    public CustomsData(String name, String phone, Long CUIT) {
        this.name = name;
        this.phone = phone;
        this.CUIT = CUIT;
    }
}
