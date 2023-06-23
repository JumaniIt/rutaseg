package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.TestEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class TestResponse {
    private final Long id;
    private final String stringField;
    private final Long longField;
    private final TestEnum enumField;

    private final ZonedDateTime dateField;
}