package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;

@EqualsAndHashCode(exclude = "createdAt")
@AllArgsConstructor
@Getter
public class OrderResponse {

    private Long clientId;
    private long createdByUserId;
    private Long id;
    private boolean pema;
    private boolean port;
    private boolean transport;
    private OrderStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime finishedAt;
    private ArrivalDataResponse arrivalData;
    private DriverDataResponse driverData;
    private CustomsDataResponse customsData;
}

