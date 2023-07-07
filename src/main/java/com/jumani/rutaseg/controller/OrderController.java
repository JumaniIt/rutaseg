package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.dto.request.*;
import com.jumani.rutaseg.dto.response.*;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.OrderRepository;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.domain.ArrivalData;
import com.jumani.rutaseg.domain.CustomsData;
import com.jumani.rutaseg.domain.DriverData;
import com.jumani.rutaseg.controller.OrderResponse;

import com.jumani.rutaseg.exception.ValidationException;

@RestController
@Transactional
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repo;

    public OrderController(OrderRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest, @Session SessionInfo session) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }



        // Crear objetos ArrivalData, CustomsData y DriverData a partir de los datos de la solicitud
        ArrivalData arrivalData = createArrivalData(orderRequest.getArrivalData());
        CustomsData customsData = createCustomsData(orderRequest.getCustomsData());
        DriverData driverData = createDriverData(orderRequest.getDriverData());

        // Crear la instancia de Order con los datos proporcionados
        Order order = new Order(
                orderRequest.isPema(),
                orderRequest.isPort(),
                orderRequest.isTransport(),
                arrivalData,
                driverData,
                customsData
        );

        // Asignar el createdByUserId de la sesión
        //order.setCreatedByUserId(session.id());


        // Realizar la lógica adicional de creación de la orden, como persistencia en la base de datos
        Order createdOrder = repo.save(order);

        // Crear la respuesta con los datos de la orden creada
        OrderResponse orderResponse = createOrderResponse(createdOrder);

        // Devolver la respuesta con el status code CREATED (201)
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }


    private ArrivalData createArrivalData(ArrivalData arrivalDataRequest) {
        // Crear una instancia de ArrivalData a partir de ArrivalDataRequest
        return new ArrivalData(
                arrivalDataRequest.getArrivalDate(),
                arrivalDataRequest.getArrivalTime(),
                arrivalDataRequest.getTurn(),
                arrivalDataRequest.isFreeLoad(),
                arrivalDataRequest.getDestination(),
                arrivalDataRequest.getFob(),
                arrivalDataRequest.getCurrency()
        );
    }

    private CustomsData createCustomsData(CustomsData customsDataRequest) {
        // Crear una instancia de CustomsData a partir de CustomsDataRequest
        return new CustomsData(
                customsDataRequest.getName(),
                customsDataRequest.getPhone(),
                customsDataRequest.getCUIT()
        );
    }

    private DriverData createDriverData(DriverData driverDataRequest) {
        // Crear una instancia de DriverData a partir de DriverDataRequest
        return new DriverData(
                driverDataRequest.getName(),
                driverDataRequest.getPhone(),
                driverDataRequest.getCompany()
        );
    }

    private OrderResponse createOrderResponse(Order order) {
        // Crear una instancia de OrderResponse a partir de Order
        return new OrderResponse(
                order.getId(),
                order.isPema(),
                order.isPort(),
                order.isTransport(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getFinishedAt(),
                order.getArrivalData(),
                order.getDriverData(),
                order.getCustomsData()
        );
    }
}