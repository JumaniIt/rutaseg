package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.ZonedDateTime;
import java.util.Optional;
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
    public Order(boolean pema, boolean port, boolean transport  ,  ArrivalData arrivalData, DriverData driverData, CustomsData customsData) {
        this.pema = false;
        this.port = false;
        this.transport = false;
        this.status = DRAFT;
        this.createdAt = this.currentDateUTC();
        this.finishedAt =null;
        this.arrivalData = arrivalData;
        this.driverData = driverData;
        this.customsData = customsData;
    }

    private Order() {
    }
}
