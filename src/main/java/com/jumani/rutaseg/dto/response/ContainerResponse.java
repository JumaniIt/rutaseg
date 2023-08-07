package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.Measures;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class ContainerResponse {

    private String code;

    private Measures measures;

    private boolean repackage;

    private String pema;


}
