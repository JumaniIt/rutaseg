package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.*;
import com.jumani.rutaseg.dto.request.*;
import com.jumani.rutaseg.dto.response.*;
import com.jumani.rutaseg.dto.result.PaginatedResult;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.OrderRepository;
import com.jumani.rutaseg.repository.client.ClientRepository;
import com.jumani.rutaseg.util.PaginationUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@Transactional
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepo;
    private final ClientRepository clientRepo;

    public OrderController(OrderRepository orderRepo, ClientRepository clientRepo) {
        this.orderRepo = orderRepo;
        this.clientRepo = clientRepo;

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable("id") long id, @Session SessionInfo session) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", id)));

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.id())) {
            throw new NotFoundException(String.format("order with id [%s] not found", id));
        }

        OrderResponse response = createOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest, @Session SessionInfo session) {
        Client client = clientRepo.findById(orderRequest.getClientId())
                .orElseThrow(() -> new NotFoundException("client not found"));

        if (!session.admin() && !Objects.equals(client.getUserId(), session.id())) {
            throw new ForbiddenException();
        }


        // Crear objetos ArrivalData, CustomsData y DriverData a partir de los datos de la solicitud
        ArrivalData arrivalData = orderRequest.getArrivalData() != null ? createArrivalData(orderRequest.getArrivalData()) : null;
        CustomsData customsData = orderRequest.getCustomsData() != null ? createCustomsData(orderRequest.getCustomsData()) : null;
        DriverData driverData = orderRequest.getDriverData() != null ? createDriverData(orderRequest.getDriverData()) : null;

        List<Container> containers = orderRequest.getContainers() != null ?
                orderRequest.getContainers().stream()
                        .map(containerRequest -> new Container(
                                containerRequest.getCode(),
                                containerRequest.getMeasures(),
                                containerRequest.isRepackage(),
                                containerRequest.getPema()
                        ))
                        .collect(Collectors.toList()) : Collections.emptyList();

        // Crear el objeto ConsigneeData a partir de los datos de ConsigneeData de la solicitud, si existe
        ConsigneeData consigneeData = orderRequest.getConsigneeData() != null ?
                new ConsigneeData(
                        orderRequest.getConsigneeData().getName(),
                        orderRequest.getConsigneeData().getCuit()
                ) : null;

        // Crear la instancia de Order con los datos proporcionados
        Order order = new Order(
                client,
                orderRequest.isPema(),
                orderRequest.isPort(),
                orderRequest.isTransport(),
                arrivalData,
                driverData,
                customsData,
                session.id(),
                containers,
                consigneeData
        );


        // Realizar la lógica adicional de creación de la orden, como persistencia en la base de datos
        Order createdOrder = orderRepo.save(order);

        // Crear la respuesta con los datos de la orden creada
        OrderResponse orderResponse = createOrderResponse(createdOrder);

        // Devolver la respuesta con el status code CREATED (201)
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable("id") long id,
            @RequestBody OrderRequest orderRequest,
            @Session SessionInfo session
    ) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", id)));

        Client client = clientRepo.findById(orderRequest.getClientId())
                .orElseThrow(() -> new NotFoundException("client not found"));

        // Verificar que la sesión sea de un usuario administrador o que la orden sea del cliente asociado al usuario de la sesión.
        if (!session.admin() && !Objects.equals(client.getUserId(), session.id())) {
            throw new ForbiddenException();
        }

        // Asegurarse de que, si la sesión no es de un usuario administrador, la orden esté en estado DRAFT.
        if (!session.admin() && order.getStatus() != OrderStatus.DRAFT) {
            throw new ForbiddenException();

        }
        // Obtener el ID del cliente actualmente asociado a la orden
        Long currentClientId = order.getClient().getId();

        // Verificar si el cliente de la solicitud coincide con el cliente actualmente asociado a la orden
        if (!session.admin() && !Objects.equals(orderRequest.getClientId(), currentClientId)) {
            throw new ForbiddenException();
        }

        // Obtener los valores de los atributos de la orden desde el objeto OrderRequest
        boolean pema = orderRequest.isPema();
        boolean port = orderRequest.isPort();
        boolean transport = orderRequest.isTransport();
        ArrivalDataRequest arrivalDataRequest = orderRequest.getArrivalData();
        DriverDataRequest driverDataRequest = orderRequest.getDriverData();
        CustomsDataRequest customsDataRequest = orderRequest.getCustomsData();
        List<ContainerRequest> containerRequests = orderRequest.getContainers();
        ConsigneeDataRequest consigneeDataRequest = orderRequest.getConsigneeData();

        // Crear objetos ArrivalData, CustomsData y DriverData a partir de los datos de la solicitud
        ArrivalData arrivalData = arrivalDataRequest != null ? createArrivalData(arrivalDataRequest) : null;
        DriverData driverData = driverDataRequest != null ? createDriverData(driverDataRequest) : null;
        CustomsData customsData = customsDataRequest != null ? createCustomsData(customsDataRequest) : null;

        // Crear una lista de objetos Container a partir de los datos de la solicitud
        List<Container> containers = containerRequests != null ?
                containerRequests.stream()
                        .map(containerRequest -> new Container(
                                containerRequest.getCode(),
                                containerRequest.getMeasures(),
                                containerRequest.isRepackage(),
                                containerRequest.getPema()
                        ))
                        .collect(Collectors.toList()) : Collections.emptyList();

        // Crear el objeto ConsigneeData a partir de los datos de la solicitud, si existe
        ConsigneeData consigneeData = consigneeDataRequest != null ?
                new ConsigneeData(
                        consigneeDataRequest.getName(),
                        consigneeDataRequest.getCuit()
                ) : null;

        // Actualizar los atributos de la orden utilizando el método update() de la clase Order
        order.update(client, pema, port, transport, arrivalData, driverData, customsData, containers, consigneeData);


        // Actualizar la orden en la base de datos
        Order updatedOrder = orderRepo.save(order);

        // Crear la respuesta con los datos actualizados de la orden
        OrderResponse orderResponse = createOrderResponse(updatedOrder);

        // Devolver la respuesta con el estado OK (200)
        return ResponseEntity.ok(orderResponse);
    }


    private ArrivalData createArrivalData(ArrivalDataRequest arrivalDataRequest) {
        // Crear una instancia de ArrivalData a partir de ArrivalDataRequest
        return new ArrivalData(
                arrivalDataRequest.getArrivalDate(),
                arrivalDataRequest.getArrivalTime(),
                arrivalDataRequest.getOrigin(),
                arrivalDataRequest.getTurn(),
                arrivalDataRequest.isFreeLoad(),
                arrivalDataRequest.getDestinationType(),
                arrivalDataRequest.getDestinationName(),
                arrivalDataRequest.getFob(),
                Optional.ofNullable(arrivalDataRequest.getCurrency()).map(String::toUpperCase).orElse(null)
        );
    }

    private CustomsData createCustomsData(CustomsDataRequest customsDataRequest) {
        // Crear una instancia de CustomsData a partir de CustomsDataRequest
        return new CustomsData(
                customsDataRequest.getName(),
                customsDataRequest.getPhone()
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

    private ConsigneeData createConsigneeData(ConsigneeDataRequest consigneeDataRequest) {
        // Crear una instancia de ConsigneeData a partir de ConsigneeDataRequest
        return new ConsigneeData(
                consigneeDataRequest.getName(),
                consigneeDataRequest.getCuit()
        );
    }

    private Container createContainer(ContainerRequest containerRequest) {
        // Crear una instancia de Container a partir de ContainerRequest
        return new Container(
                containerRequest.getCode(),
                containerRequest.getMeasures(),
                containerRequest.isRepackage(),
                containerRequest.getPema()
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
                    arrivalData.getOrigin(),
                    arrivalData.getTurn(),
                    arrivalData.isFreeLoad(),
                    arrivalData.getDestinationType(),
                    arrivalData.getDestinationName(),
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
                    customsData.getPhone()
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
        // Crear una instancia de ConsigneeDataResponse a partir de ConsigneeData, si existe
        ConsigneeDataResponse consigneeDataResponse = null;
        ConsigneeData consigneeData = order.getConsignee();
        if (consigneeData != null) {
            consigneeDataResponse = new ConsigneeDataResponse(
                    consigneeData.getName(),
                    consigneeData.getCuit()
            );
        }

        // Crear una lista de ContainerResponse a partir de los objetos Container
        List<ContainerResponse> containerResponse = order.getContainers().stream()
                .map(container -> new ContainerResponse(
                        container.getCode(),
                        container.getMeasures(),
                        container.isRepackage(),
                        container.getPema()
                ))
                .collect(Collectors.toList());


        // Crear una instancia de OrderResponse con los datos de ArrivalDataResponse, CustomsDataResponse y DriverDataResponse
        return new OrderResponse(
                order.getId(),
                order.getClientId(),
                order.getCreatedByUserId(),
                order.isPema(),
                order.isPort(),
                order.isTransport(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getFinishedAt(),
                arrivalDataResponse,
                driverDataResponse,
                customsDataResponse,
                containerResponse,
                consigneeDataResponse


        );
    }

    @GetMapping
    public ResponseEntity<PaginatedResult<OrderResponse>> search(
            @RequestParam(value = "pema", required = false) Boolean pema,
            @RequestParam(value = "transport", required = false) Boolean transport,
            @RequestParam(value = "port", required = false) Boolean port,
            @RequestParam(value = "arrivalDateFrom", required = false) LocalDate arrivalDateFrom,
            @RequestParam(value = "arrivalDateTo", required = false) LocalDate arrivalDateTo,
            @RequestParam(value = "arrivalTimeFrom", required = false) LocalTime arrivalTimeFrom,
            @RequestParam(value = "arrivalTimeTo", required = false) LocalTime arrivalTimeTo,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "pageSize", required = false, defaultValue = "1") Integer pageSize,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @Session SessionInfo session
    ) {
        final Long theClientId;
        if (!session.admin()) {
            Optional<Client> clientOptional = clientRepo.findOneByUser_Id(session.id());
            if (clientOptional.isPresent()) {
                theClientId = clientOptional.get().getId();
            } else {
                return ResponseEntity.ok(new PaginatedResult<>(0, 0, page, 1, Collections.emptyList()));
            }
        } else {
            theClientId = clientId;
        }

        if (Objects.nonNull(arrivalDateFrom) && Objects.nonNull(arrivalDateTo) && arrivalDateFrom.isAfter(arrivalDateTo)) {
            throw new ValidationException("invalid_date_range", "the 'arrivalDateFrom' cannot be after 'arrivalDateTo'");
        }

        if (Objects.nonNull(arrivalTimeFrom) && Objects.nonNull(arrivalTimeTo)
                && arrivalDateFrom.equals(arrivalDateTo) && arrivalTimeFrom.isAfter(arrivalTimeTo)) {
            throw new ValidationException("invalid_time_range", "the 'arrivalTimeFrom' cannot be after 'arrivalTimeTo'");
        }

        long totalElements = orderRepo.count(
                pema,
                transport,
                port,
                arrivalDateFrom,
                arrivalDateTo,
                arrivalTimeFrom,
                arrivalTimeTo,
                theClientId,
                status
        );

        final PaginatedResult<OrderResponse> result = PaginationUtil.get(totalElements, pageSize, page, (offset, limit) -> {
            List<Order> orders = orderRepo.search(
                    pema,
                    transport,
                    port,
                    arrivalDateFrom,
                    arrivalDateTo,
                    arrivalTimeFrom,
                    arrivalTimeTo,
                    theClientId,
                    status,
                    offset,
                    limit
            );

            return orders.stream()
                    .map(this::createOrderResponse)
                    .toList();
        });

        return ResponseEntity.ok(result);
    }

}