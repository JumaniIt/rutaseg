package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.Currency;
import com.jumani.rutaseg.domain.Destination;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class ArrivalDataResponse {

    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String turn;
    private boolean freeLoad;
    private Destination destination;
    private String fob;
    private Currency currency;
}
