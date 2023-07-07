package com.jumani.rutaseg.dto.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DriverDataRequest {

    private String name;
    private String phone;
    private String company;

}
