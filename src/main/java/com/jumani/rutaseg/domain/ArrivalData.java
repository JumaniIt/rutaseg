package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name ="Arivals_data")
@Slf4j
public class ArrivalData implements DateGen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "turn")
    private String turn;

    @Column(name = "free_load")
    private boolean freeLoad;

    @Column(name = "destination")
    private Destination destination;

    @Column(name = "fob")
    private String fob;

    @Column(name = "currency")
    private Currency currency;

    private ArrivalData() {
    }

    public ArrivalData(LocalDate arrivalDate, LocalTime arrivalTime, String turn, boolean freeLoad,
                       Destination destination, String fob, Currency currency) {
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.turn = turn;
        this.freeLoad = freeLoad;
        this.destination = destination;
        this.fob = fob;
        this.currency = currency;
    }
}
