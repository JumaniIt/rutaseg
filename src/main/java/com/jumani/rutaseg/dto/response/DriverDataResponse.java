package com.jumani.rutaseg.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class DriverDataResponse {

    private String name;
    private String phone;
    private String company;
}
