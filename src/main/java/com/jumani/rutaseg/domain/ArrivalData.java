package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Getter

public class ArrivalData implements DateGen {


    private ZonedDateTime arrivalDate;

    private ZonedDateTime arrivalTime;

    private String turn;

    private boolean freeLoad;

    private Type type;

    private String fob;

    private Currency currency;

    private ArrivalData() {
    }

    public ArrivalData(ZonedDateTime arrivalDate, ZonedDateTime arrivalTime, String turn, boolean freeLoad, Type type, String fob, Currency currency) {
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.turn = turn;
        this.freeLoad = freeLoad;
        this.type = type;
        this.fob = fob;
        this.currency = currency;
    }
}
