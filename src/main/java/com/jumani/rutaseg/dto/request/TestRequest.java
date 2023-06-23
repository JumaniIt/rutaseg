package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.TestEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TestRequest {
    private String stringField;
    private Long longField;
    private TestEnum enumField;
}
