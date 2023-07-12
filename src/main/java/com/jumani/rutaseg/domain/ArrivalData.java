package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "arrival_datas")
@Slf4j
public class ArrivalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "origin")
    @Enumerated(EnumType.STRING)
    private Origin origin;

    @Column(name = "turn")
    private String turn;

    @Column(name = "free_load")
    private boolean freeLoad;

    @Column(name = "destination_type")
    private DestinationType destinationType;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "fob")
    private String fob;

    @Column(name = "currency")
    private String currency;

    private ArrivalData() {
    }

    public ArrivalData(LocalDate arrivalDate, LocalTime arrivalTime,
                       Origin origin, String turn, boolean freeLoad,
                       DestinationType destinationType, String destinationName,
                       String fob, String currency) {

        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.turn = turn;
        this.freeLoad = freeLoad;
        this.destinationType = destinationType;
        this.destinationName = destinationName;
        this.fob = fob;
        this.currency = currency;
    }
}
