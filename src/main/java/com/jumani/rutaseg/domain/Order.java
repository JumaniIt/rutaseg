package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

import static com.jumani.rutaseg.domain.OrderStatus.DRAFT;

@Getter
@Entity
@Table(name = "orders")
@Slf4j
public class Order implements DateGen {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pema")
    private boolean pema;

    @Column(name = "port")
    private boolean port;

    @Column(name = "transport")
    private boolean transport;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "finished_at")
    private ZonedDateTime finishedAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id")
    private ArrivalData arrivalData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id")
    private DriverData driverData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id")
    private CustomsData customsData;

    //constructor
    public Order(boolean pema, boolean port, boolean transport,
                 ArrivalData arrivalData,
                 DriverData driverData,
                 CustomsData customsData) {

        this.pema = pema;
        this.port = port;
        this.transport = transport;
        this.status = DRAFT;
        this.createdAt = this.currentDateUTC();
        this.finishedAt = null;
        this.arrivalData = arrivalData;
        this.driverData = driverData;
        this.customsData = customsData;
    }

    private Order() {
    }
}
