package com.jumani.rutaseg.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ClientRequest {
    @NotEmpty
    private String phone;
    @NotNull
    @Positive
    private Long cuit;

    @Positive
    private Long userId;
}
