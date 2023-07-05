package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.jumani.rutaseg.domain.OrderStatus.DRAFT;

@Getter
public class Order implements DateGen {

    private boolean pema;

    private boolean port;

    private boolean transport;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private ZonedDateTime createdAt;

    private ZonedDateTime finishedAt;

    private ArrivalData arrivalData;

    private DriverData driverData;

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
