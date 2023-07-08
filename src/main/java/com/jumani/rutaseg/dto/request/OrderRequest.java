package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.ArrivalData;
import com.jumani.rutaseg.domain.CustomsData;
import com.jumani.rutaseg.domain.DriverData;
import com.jumani.rutaseg.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@Getter
public class OrderRequest {
    private boolean pema;
    private boolean port;
    private boolean transport;
    private ArrivalDataRequest arrivalData;
    private DriverDataRequest driverData;
    private CustomsDataRequest customsData;
}