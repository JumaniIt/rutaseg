package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.Currency;
import com.jumani.rutaseg.domain.Destination;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class ArrivalDataResponse {

    private Long id;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String turn;
    private boolean freeLoad;
    private Destination destination;
    private String fob;
    private Currency currency;
}
