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
    @JoinColumn(name = "_arrival_data_id")
    private ArrivalData arrivalData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "driver_data_id")
    private DriverData driverData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customs_data_id")
    private CustomsData customsData;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client  client;

    @Column(name = "created_by_user_id")
    private long createdByUserId;

    //constructor
    public Order(boolean pema, boolean port, boolean transport,
                 ArrivalData arrivalData,
                 DriverData driverData,
                 CustomsData customsData,long createdByUserId,Client client) {

        this.pema = pema;
        this.port = port;
        this.transport = transport;
        this.status = DRAFT;
        this.createdAt = this.currentDateUTC();
        this.finishedAt = null;
        this.arrivalData = arrivalData;
        this.driverData = driverData;
        this.customsData = customsData;
        this.createdByUserId = createdByUserId;
        this.client= client;
    }
    public Long getClientId() {
        return this.client.getId();
    }

    private Order() {
    }
}
