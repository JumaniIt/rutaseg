package com.jumani.rutaseg.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomsDataResponse {

    private Long id;
    private String name;
    private String phone;
    private Long CUIT;
}
