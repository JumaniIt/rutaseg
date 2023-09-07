package com.jumani.rutaseg.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderRequest {

    private String code;
    @NotNull
    @Positive
    @JsonProperty("client_id")
    private Long clientId;
    private boolean pema;
    private boolean port;
    private boolean transport;
    @JsonProperty("arrival_data")
    private ArrivalDataRequest arrivalData;

    @JsonProperty("driver_data")
    private DriverDataRequest driverData;

    @JsonProperty("customs_data")
    private CustomsDataRequest customsData;
    private List<ContainerRequest> containers;

    @JsonProperty("consignee_data")
    private ConsigneeDataRequest consigneeData;
}
