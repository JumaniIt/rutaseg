package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.DestinationType;
import com.jumani.rutaseg.domain.Terminal;
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
    private Terminal terminal;
    private boolean freeLoad;
    private DestinationType destinationType;
    private String destinationCode;
    private String fob;
    private String currency;
}
