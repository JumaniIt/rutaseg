package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.Measures;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ContainerRequest {

    private String code;

    private Measures measures;

    private boolean repackage;

    private String pema;

}
