package com.jumani.rutaseg.service.order;

import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.OrderStatus;
import com.jumani.rutaseg.domain.Sort;
import com.jumani.rutaseg.dto.result.PaginatedResult;
import com.jumani.rutaseg.repository.OrderRepository;
import com.jumani.rutaseg.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RequiredArgsConstructor
public class OrderSearchService {
    private final Map<SearchParamsKey, PaginatedResult<Order>> cache;

    private final OrderRepository orderRepo;

    public PaginatedResult<Order> search(String code, Boolean pema, Boolean transport, Boolean port,
                                         LocalDate arrivalDateFrom, LocalDate arrivalDateTo,
                                         LocalTime arrivalTimeFrom, LocalTime arrivalTimeTo,
                                         Long clientId,
                                         OrderStatus status,
                                         List<Sort> sorts, // Cambiado a List<Sort>
                                         int pageSize,
                                         int page) {

        final SearchParamsKey key = new SearchParamsKey(code, pema, transport, port,
                arrivalDateFrom, arrivalDateTo, arrivalTimeFrom, arrivalTimeTo,
                clientId, status, pageSize, page);

        if (!cache.containsKey(key)) {
            final PaginatedResult<Order> result = this.doSearch(code, pema, transport, port,
                    arrivalDateFrom, arrivalDateTo, arrivalTimeFrom, arrivalTimeTo,
                    clientId, status, sorts, pageSize, page); // Pasamos la lista de Sort

            cache.put(key, result);
            return result;
        }

        return cache.get(key);
    }

    private PaginatedResult<Order> doSearch(String code, Boolean pema, Boolean transport, Boolean port,
                                            LocalDate arrivalDateFrom, LocalDate arrivalDateTo,
                                            LocalTime arrivalTimeFrom, LocalTime arrivalTimeTo,
                                            Long clientId,
                                            OrderStatus status,
                                            List<Sort> sorts, // Cambiado a List<Sort>
                                            int pageSize,
                                            int page) {
        final long totalElements = orderRepo.count(
                code,
                pema,
                transport,
                port,
                arrivalDateFrom,
                arrivalDateTo,
                arrivalTimeFrom,
                arrivalTimeTo,
                clientId,
                status
        );

        return PaginationUtil.get(totalElements, pageSize, page, (offset, limit) ->
                orderRepo.search(
                        code, // Pasamos la lista de Sort
                        pema,
                        transport,
                        port,
                        arrivalDateFrom,
                        arrivalDateTo,
                        arrivalTimeFrom,
                        arrivalTimeTo,
                        clientId,
                        status,
                        sorts,
                        offset,
                        limit
                                )
        );
    }

    public record SearchParamsKey(
            String code, Boolean pema, Boolean transport, Boolean port,
            LocalDate arrivalDateFrom, LocalDate arrivalDateTo,
            LocalTime arrivalTimeFrom, LocalTime arrivalTimeTo,
            Long clientId, OrderStatus status,
            int pageSize, int page) {
    }
}