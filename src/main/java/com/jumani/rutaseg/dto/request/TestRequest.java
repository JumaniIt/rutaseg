package com.jumani.rutaseg.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jumani.rutaseg.domain.TestEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@Getter
public class TestRequest {
    private String stringField;
    private Long longField;
    private TestEnum enumField;
}
