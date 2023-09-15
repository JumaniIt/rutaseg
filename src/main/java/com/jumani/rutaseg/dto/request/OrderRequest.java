package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.Terminal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderRequest {

    private String code;
    @NotNull
    @Positive
    private Long clientId;
    private boolean pema;
    private boolean port;
    private boolean transport;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private Terminal origin;
    private Terminal target;
    private Boolean freeLoad;
    private DriverDataRequest driverData;
    private CustomsDataRequest customsData;
    private List<ContainerRequest> containers;
    private List<FreeLoadRequest> freeLoads;
    private ConsigneeDataRequest consigneeData;

    public boolean isFreeLoad() {
        return Optional.ofNullable(this.freeLoad).orElse(false);
    }
}
