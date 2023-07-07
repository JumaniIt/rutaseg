package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.Currency;
import com.jumani.rutaseg.domain.Destination;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
public class ArrivalDataRequest {


    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String turn;
    private boolean freeLoad;
    private Destination destination;
    private String fob;


    private Currency currency;
}
