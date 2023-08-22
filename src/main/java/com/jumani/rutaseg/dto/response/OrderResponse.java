package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.OrderStatus;
import com.jumani.rutaseg.dto.request.ConsigneeDataRequest;
import com.jumani.rutaseg.dto.request.ContainerRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@EqualsAndHashCode(exclude = "createdAt")
@AllArgsConstructor
@Getter
public class OrderResponse {

    private Long id;
    private long clientId;
    private long createdByUserId;
    private boolean pema;
    private boolean port;
    private boolean transport;
    private OrderStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime finishedAt;
    private ArrivalDataResponse arrivalData;
    private DriverDataResponse driverData;
    private CustomsDataResponse customsData;
    private List<ContainerResponse> containers;
    private ConsigneeDataResponse consigneeData;
    private DocumentResponse documentResponse;

}

