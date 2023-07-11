package com.jumani.rutaseg.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderRequest {
    @NotNull
    @Positive
    private Long clientId;
    private boolean pema;
    private boolean port;
    private boolean transport;
    private ArrivalDataRequest arrivalData;
    private DriverDataRequest driverData;
    private CustomsDataRequest customsData;
}