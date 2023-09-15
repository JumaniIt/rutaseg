package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "arrival_datas")
@Slf4j
@FieldNameConstants
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
    private Terminal terminal;

    @Column(name = "free_load")
    private boolean freeLoad;

    @Column(name = "destination_type")
    @Enumerated(EnumType.STRING)
    private DestinationType destinationType;

    @Column(name = "destination_code")
    private String destinationCode;

    @Column(name = "fob")
    private String fob;

    @Column(name = "currency")
    private String currency;

    @Column(name = "product_details", columnDefinition = "text")
    private String productDetails;

    private ArrivalData() {
    }

    public ArrivalData(LocalDate arrivalDate, LocalTime arrivalTime,
                       Terminal terminal, boolean freeLoad,
                       DestinationType destinationType, String destinationCode,
                       String fob, String currency,
                       String productDetails) {

        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.terminal = terminal;
        this.freeLoad = freeLoad;
        this.destinationType = destinationType;
        this.destinationCode = destinationCode;
        this.fob = fob;
        this.currency = currency;
        this.productDetails = productDetails;
    }
}
