package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.OrderStatus;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface OrderRepositoryExtended {

    List<Order> search(@Nullable Boolean pema,
                       @Nullable Boolean transport,
                       @Nullable Boolean port,
                       @Nullable LocalDate arrivalDateFrom,
                       @Nullable LocalDate arrivalDateTo,
                       @Nullable LocalTime arrivalTimeFrom,
                       @Nullable LocalTime arrivalTimeTo,
                       @Nullable Long clientId,
                       @Nullable OrderStatus status,
                       int pageSize,
                       int offset);

    long count(@Nullable Boolean pema,
               @Nullable Boolean transport,
               @Nullable Boolean port,
               @Nullable LocalDate arrivalDateFrom,
               @Nullable LocalDate arrivalDateTo,
               @Nullable LocalTime arrivalTimeFrom,
               @Nullable LocalTime arrivalTimeTo,
               @Nullable Long clientId,
               @Nullable OrderStatus status);
}
