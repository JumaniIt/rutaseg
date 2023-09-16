package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.ContainerType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class ContainerResponse {

    private String code;

    private ContainerType type;

    private boolean repackage;

    private String pema;

    private List<DestinationResponse> destinations;

}
