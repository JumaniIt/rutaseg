package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.util.DateGen;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ArrivalData implements DateGen {

    private LocalDate  arrivalDate;

    private LocalTime arrivalTime;

    private String turn;

    private boolean freeLoad;

    private Destination destination;

    private String fob;

    private Currency currency;

    private ArrivalData() {
    }

    public ArrivalData(LocalDate  arrivalDate, LocalTime arrivalTime, String turn, boolean freeLoad, Destination destination, String fob, Currency currency) {
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.turn = turn;
        this.freeLoad = freeLoad;
        this.destination = destination;
        this.fob = fob;
        this.currency = currency;
    }
}
