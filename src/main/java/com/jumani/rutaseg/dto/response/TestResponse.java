package com.jumani.rutaseg.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jumani.rutaseg.domain.TestEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
public class TestResponse {
    private final Long id;
    private final String stringField;
    private final Long longField;
    private final TestEnum enumField;
}