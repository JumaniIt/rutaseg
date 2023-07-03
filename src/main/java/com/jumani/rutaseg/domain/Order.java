package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

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


    //setter
    public void setPema(boolean pema) {
        this.pema = pema;
    }

    public void setPort(boolean port) {
        this.port = port;
    }

    public void setTransport(boolean transport) {
        this.transport = transport;
    }

    public void setArrivalData(ArrivalData arrivalData) {
        this.arrivalData = arrivalData;
    }

    public void setDriverData(DriverData driverData) {
        this.driverData = driverData;
    }

    public void setCustomsData(CustomsData customsData) {
        this.customsData = customsData;
    }

    //constructor
    public Order(boolean pema, boolean port, boolean transport, OrderStatus status, ZonedDateTime createdAt, ZonedDateTime finishedAt, ArrivalData arrivalData, DriverData driverData, CustomsData customsData) {
        this.pema = false;
        this.port = false;
        this.transport = false;
        this.status = status;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
        this.arrivalData = arrivalData;
        this.driverData = driverData;
        this.customsData = customsData;
    }


    private Order() {

    }




}
