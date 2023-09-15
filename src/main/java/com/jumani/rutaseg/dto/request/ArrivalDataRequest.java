package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.DestinationType;
import com.jumani.rutaseg.domain.Origin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ArrivalDataRequest {
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private Origin origin;
    private boolean freeLoad;
    private DestinationType destinationType;
    private String destinationCode;
    private String fob;
    private String currency;
    private String productDetails;
}
