package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.ContainerType;
import com.jumani.rutaseg.dto.request.DestinationRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class ContainerResponse {

    private String code;

    private ContainerType containerType;

    private boolean repackage;

    private String pema;

    private List<DestinationResponse> destinations;

}
