package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.jumani.rutaseg.domain.OrderStatus.DRAFT;

@Getter
@Entity
@Table(name = "orders")
@Slf4j
public class Order implements DateGen {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "fk_orders_clients"))
    private Client client;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private ArrivalData arrivalData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private DriverData driverData;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private CustomsData customsData;

    @Column(name = "created_by_user_id")
    private long createdByUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "containers",
            joinColumns = @JoinColumn(name = "order_id"))
    private List<Container> containers;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private ConsigneeData consignee;

    //constructor
    public Order(Client client,
                 boolean pema, boolean port, boolean transport,
                 ArrivalData arrivalData,
                 DriverData driverData,
                 CustomsData customsData,
                 long createdByUserId, List<Container> containers, ConsigneeData consignee) {

        this.client = client;
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
        this.containers = containers;
        this.consignee = consignee;
    }

    public Long getClientId() {
        return this.client.getId();
    }

    private Order() {
    }

    public void update(Client client, boolean pema, boolean port, boolean transport,
                       ArrivalData arrivalData, DriverData driverData,
                       CustomsData customsData, List<Container> containers, ConsigneeData consignee) {


            this.client = client;
            this.pema = pema;
            this.port = port;
            this.transport = transport;
            this.arrivalData = arrivalData;
            this.driverData = driverData;
            this.customsData = customsData;
            this.containers = containers;
            this.consignee = consignee;

    }
}
