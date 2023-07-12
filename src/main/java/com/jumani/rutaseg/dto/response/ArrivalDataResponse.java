package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.Currency;
import com.jumani.rutaseg.domain.DestinationType;
import com.jumani.rutaseg.domain.Origin;
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
    private Origin origin;
    private String turn;
    private boolean freeLoad;
    private DestinationType destinationType;
    private String destinationName;
    private String fob;
    private String currency;
}
