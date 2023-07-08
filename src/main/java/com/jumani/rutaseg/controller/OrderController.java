package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.ArrivalData;
import com.jumani.rutaseg.domain.CustomsData;
import com.jumani.rutaseg.domain.DriverData;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.dto.request.ArrivalDataRequest;
import com.jumani.rutaseg.dto.request.CustomsDataRequest;
import com.jumani.rutaseg.dto.request.DriverDataRequest;
import com.jumani.rutaseg.dto.request.OrderRequest;
import com.jumani.rutaseg.dto.response.*;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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


        // Crear objetos ArrivalData, CustomsData y DriverData a partir de los datos de la solicitud
        ArrivalData arrivalData = orderRequest.getArrivalData() != null ? createArrivalData(orderRequest.getArrivalData()) : null;
        CustomsData customsData = orderRequest.getCustomsData() != null ? createCustomsData(orderRequest.getCustomsData()) : null;
        DriverData driverData = orderRequest.getDriverData() != null ? createDriverData(orderRequest.getDriverData()) : null;

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


    private ArrivalData createArrivalData(ArrivalDataRequest arrivalDataRequest) {
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

    private CustomsData createCustomsData(CustomsDataRequest customsDataRequest) {
        // Crear una instancia de CustomsData a partir de CustomsDataRequest
        return new CustomsData(
                customsDataRequest.getName(),
                customsDataRequest.getPhone(),
                customsDataRequest.getCuit()
        );
    }

    private DriverData createDriverData(DriverDataRequest driverDataRequest) {
        // Crear una instancia de DriverData a partir de DriverDataRequest
        return new DriverData(
                driverDataRequest.getName(),
                driverDataRequest.getPhone(),
                driverDataRequest.getCompany()
        );
    }

    private OrderResponse createOrderResponse(Order order) {
        // Crear una instancia de ArrivalDataResponse a partir de ArrivalData
        ArrivalDataResponse arrivalDataResponse = null;
        ArrivalData arrivalData = order.getArrivalData();
        if (arrivalData != null) {
            arrivalDataResponse = new ArrivalDataResponse(
                    arrivalData.getArrivalDate(),
                    arrivalData.getArrivalTime(),
                    arrivalData.getTurn(),
                    arrivalData.isFreeLoad(),
                    arrivalData.getDestination(),
                    arrivalData.getFob(),
                    arrivalData.getCurrency()
            );
        }

        // Crear una instancia de CustomsDataResponse a partir de CustomsData
        CustomsDataResponse customsDataResponse = null;
        CustomsData customsData = order.getCustomsData();
        if (customsData != null) {
            customsDataResponse = new CustomsDataResponse(
                    customsData.getName(),
                    customsData.getPhone(),
                    customsData.getCuit()
            );
        }

        // Crear una instancia de DriverDataResponse a partir de DriverData
        DriverDataResponse driverDataResponse = null;
        DriverData driverData = order.getDriverData();
        if (driverData != null) {
            driverDataResponse = new DriverDataResponse(
                    driverData.getName(),
                    driverData.getPhone(),
                    driverData.getCompany()
            );
        }

        // Crear una instancia de OrderResponse con los datos de ArrivalDataResponse, CustomsDataResponse y DriverDataResponse
        return new OrderResponse(
                order.getId(),
                order.isPema(),
                order.isPort(),
                order.isTransport(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getFinishedAt(),
                arrivalDataResponse,
                driverDataResponse,
                customsDataResponse
        );
    }
}