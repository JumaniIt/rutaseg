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

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.userId())) {
            throw new NotFoundException(String.format("order with id [%s] not found", id));
        }

        OrderResponse response = createOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest, @Session SessionInfo session) {
        Client client = clientRepo.findById(orderRequest.getClientId())
                .orElseThrow(() -> new NotFoundException("client not found"));

        if (!session.admin() && !Objects.equals(client.getUserId(), session.userId())) {
            throw new ForbiddenException();
        }


        // Crear objetos ArrivalData, CustomsData y DriverData a partir de los datos de la solicitud
        CustomsData customsData = orderRequest.getCustomsData() != null ? createCustomsData(orderRequest.getCustomsData()) : null;
        DriverData driverData = orderRequest.getDriverData() != null ? createDriverData(orderRequest.getDriverData()) : null;

        List<Container> containers = orderRequest.getContainers() != null ?
                orderRequest.getContainers().stream()
                        .map(containerRequest -> new Container(
                                containerRequest.getCode(),
                                containerRequest.getContainerType(),
                                containerRequest.isRepackage(),
                                containerRequest.getBl(),
                                containerRequest.getPema(),
                                Optional.ofNullable(containerRequest.getDestinations()).orElse(Collections.emptyList())
                                        .stream().map(d -> new Destination(d.getType(), d.getCode(), d.getFob(), d.getCurrency(), d.getProductDetails()
                                        )).toList()))
                        .collect(Collectors.toList()) : Collections.emptyList();

        List<FreeLoad> freeLoads = Optional.ofNullable(orderRequest.getFreeLoads()).orElse(Collections.emptyList())
                .stream()
                .map(flr -> new FreeLoad(flr.getPatent(), flr.getType(), flr.getWeight(), flr.getGuide(), flr.getPema(),
                        Optional.ofNullable(flr.getDestinations()).orElse(Collections.emptyList())
                                .stream().map(d -> new Destination(d.getType(), d.getCode(), d.getFob(), d.getCurrency(), d.getProductDetails()
                                )).toList()))
                .toList();

        // Crear el objeto ConsigneeData a partir de los datos de ConsigneeData de la solicitud, si existe
        ConsigneeData consigneeData = orderRequest.getConsigneeData() != null ?
                new ConsigneeData(
                        orderRequest.getConsigneeData().getName(),
                        orderRequest.getConsigneeData().getCuit()
                ) : null;

        // Crear la instancia de Order con los datos proporcionados
        Order order = new Order(
                orderRequest.getCode(),
                client,
                orderRequest.isPema(),
                orderRequest.isPort(),
                orderRequest.isTransport(),
                orderRequest.getArrivalDate(), orderRequest.getArrivalTime(),
                orderRequest.getOrigin(), orderRequest.getTarget(), orderRequest.isFreeLoad(),
                driverData,
                customsData,
                containers, freeLoads, consigneeData, session.userId()
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
            @RequestBody @Valid OrderRequest orderRequest,
            @Session SessionInfo session
    ) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", id)));

        Client client = clientRepo.findById(orderRequest.getClientId())
                .orElseThrow(() -> new NotFoundException("client not found"));

        // Verificar que la sesión sea de un usuario administrador o que la orden sea del cliente asociado al usuario de la sesión.
        if (!session.admin() && !Objects.equals(client.getUserId(), session.userId())) {
            throw new ForbiddenException();
        }

        // Asegurarse de que, si la sesión no es de un usuario administrador, la orden esté en estado DRAFT.
        if (!session.admin() && order.getStatus() != OrderStatus.DRAFT) {
            throw new ValidationException("order_not_updatable",
                    String.format("cannot update an order with status [%s]", order.getStatus()));
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
        DriverDataRequest driverDataRequest = orderRequest.getDriverData();
        CustomsDataRequest customsDataRequest = orderRequest.getCustomsData();
        List<ContainerRequest> containerRequests = orderRequest.getContainers();
        ConsigneeDataRequest consigneeDataRequest = orderRequest.getConsigneeData();

        // Crear objetos ArrivalData, CustomsData y DriverData a partir de los datos de la solicitud
        DriverData driverData = driverDataRequest != null ? createDriverData(driverDataRequest) : null;
        CustomsData customsData = customsDataRequest != null ? createCustomsData(customsDataRequest) : null;

        // Crear una lista de objetos Container a partir de los datos de la solicitud
        List<Container> containers = containerRequests != null ?
                containerRequests.stream()
                        .map(containerRequest -> new Container(
                                containerRequest.getCode(),
                                containerRequest.getContainerType(),
                                containerRequest.isRepackage(),
                                containerRequest.getBl(),
                                containerRequest.getPema(),
                                Optional.ofNullable(containerRequest.getDestinations()).orElse(Collections.emptyList())
                                        .stream().map(d -> new Destination(d.getType(), d.getCode(), d.getFob(), d.getCurrency(), d.getProductDetails()
                                        )).toList()))
                        .collect(Collectors.toList()) : Collections.emptyList();

        List<FreeLoad> freeLoads = Optional.ofNullable(orderRequest.getFreeLoads()).orElse(Collections.emptyList())
                .stream()
                .map(flr -> new FreeLoad(flr.getPatent(), flr.getType(), flr.getWeight(), flr.getGuide(), flr.getPema(),
                        Optional.ofNullable(flr.getDestinations()).orElse(Collections.emptyList())
                                .stream().map(d -> new Destination(d.getType(), d.getCode(), d.getFob(), d.getCurrency(), d.getProductDetails()
                                )).toList()))
                .toList();

        // Crear el objeto ConsigneeData a partir de los datos de la solicitud, si existe
        ConsigneeData consigneeData = consigneeDataRequest != null ?
                new ConsigneeData(
                        consigneeDataRequest.getName(),
                        consigneeDataRequest.getCuit()
                ) : null;

        // Actualizar los atributos de la orden utilizando el método update() de la clase Order
        order.update(orderRequest.getCode(), client, pema, port, transport,
                orderRequest.getArrivalDate(), orderRequest.getArrivalTime(),
                orderRequest.getOrigin(), orderRequest.getTarget(), orderRequest.getFreeLoad(),
                driverData, customsData, containers, freeLoads, consigneeData);

        order.addSystemNote(String.format("usuario [%s] de tipo [%s] actualizó datos de solicitud", session.userId(),
                session.getUserType().getTranslation()));

        // Actualizar la orden en la base de datos
        Order updatedOrder = orderRepo.save(order);

        // Crear la respuesta con los datos actualizados de la orden
        OrderResponse orderResponse = createOrderResponse(updatedOrder);

        // Devolver la respuesta con el estado OK (200)
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping
    public ResponseEntity<PaginatedResult<OrderResponse>> search(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "pema", required = false) Boolean pema,
            @RequestParam(value = "transport", required = false) Boolean transport,
            @RequestParam(value = "port", required = false) Boolean port,
            @RequestParam(value = "date_from", required = false) LocalDate arrivalDateFrom,
            @RequestParam(value = "date_to", required = false) LocalDate arrivalDateTo,
            @RequestParam(value = "time_from", required = false) LocalTime arrivalTimeFrom,
            @RequestParam(value = "time_to", required = false) LocalTime arrivalTimeTo,
            @RequestParam(value = "client_id", required = false) Long clientId,
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @Session SessionInfo session
    ) {
        final Long theClientId;
        if (!session.admin()) {
            Optional<Client> clientOptional = clientRepo.findOneByUser_Id(session.userId());
            if (clientOptional.isPresent()) {
                theClientId = clientOptional.get().getId();
            } else {
                return ResponseEntity.ok(new PaginatedResult<>(0, 0, page, 1, Collections.emptyList()));
            }
        } else {
            theClientId = clientId;
        }

        if (Objects.nonNull(arrivalDateFrom) && Objects.nonNull(arrivalDateTo) && arrivalDateFrom.isAfter(arrivalDateTo)) {
            throw new ValidationException("invalid_date_range", "date_from cannot be after date_to");
        }

        if (Objects.nonNull(arrivalTimeFrom) && Objects.nonNull(arrivalTimeTo)
                && arrivalDateFrom.equals(arrivalDateTo) && arrivalTimeFrom.isAfter(arrivalTimeTo)) {
            throw new ValidationException("invalid_time_range", "time_from cannot be after time_to");
        }

        final long totalElements = orderRepo.count(
                code,
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
                    code,
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
                    .map(this::createLightOrderResponse)
                    .toList();
        });

        return ResponseEntity.ok(result);
    }

    private OrderResponse createLightOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCode(),
                order.getClientId(),
                order.getCreatedByUserId(),
                order.isPema(),
                order.isPort(),
                order.isTransport(),
                order.getArrivalDate(),
                order.getArrivalTime(),
                order.getOrigin(),
                order.getTarget(),
                order.isFreeLoad(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getFinishedAt(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                order.isReturned(),
                order.isBilled()
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
                driverDataRequest.getChasis(),
                driverDataRequest.getSemi(),
                driverDataRequest.getCompany()
        );
    }

    private OrderResponse createOrderResponse(Order order) {
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
                    driverData.getChasis(),
                    driverData.getSemi(),
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
                        container.getType(),
                        container.isRepackage(),
                        container.getPema(),
                        container.getDestinations().stream().map(d -> new DestinationResponse(
                                        d.getType(), d.getCode(), d.getFob(), d.getCurrency(), d.getProductDetails()))
                                .toList()
                ))
                .collect(Collectors.toList());

        // Crear una lista de DocumentResponse a partir de los objetos Document
        List<DocumentResponse> documentResponse = order.getDocuments().stream()
                .map(document -> new DocumentResponse(
                        document.getId(),
                        document.getCreatedAt(),
                        document.getName(),
                        document.getResource(),
                        null
                ))
                .collect(Collectors.toList());

        // Crear una lista de CostResponse a partir de los objetos Cost
        List<CostResponse> costResponse = order.getCosts().stream()
                .map(cost -> new CostResponse(
                        cost.getId(),
                        cost.getAmount(),
                        cost.getDescription(),
                        cost.getType(),
                        cost.getCreatedAt()
                ))
                .collect(Collectors.toList());

        final List<FreeLoadResponse> freeLoadResponse = order.getFreeLoads().stream()
                .map(fl -> new FreeLoadResponse(fl.getPatent(), fl.getType(), fl.getWeight(), fl.getGuide(), fl.getPema()))
                .toList();

        // Crear una instancia de OrderResponse con los datos de ArrivalDataResponse, CustomsDataResponse y DriverDataResponse
        return new OrderResponse(
                order.getId(),
                order.getCode(),
                order.getClientId(),
                order.getCreatedByUserId(),
                order.isPema(),
                order.isPort(),
                order.isTransport(),
                order.getArrivalDate(),
                order.getArrivalTime(),
                order.getOrigin(),
                order.getTarget(),
                order.isFreeLoad(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getFinishedAt(),
                driverDataResponse,
                customsDataResponse,
                containerResponse,
                freeLoadResponse,
                consigneeDataResponse,
                documentResponse,
                costResponse,
                order.isReturned(),
                order.isBilled()
        );
    }

    @PutMapping("/{id}/status/{newStatus}")
    public ResponseEntity<OrderResponse> changeOrderStatus(
            @PathVariable("id") long id,
            @PathVariable("newStatus") OrderStatus newStatus,
            @Session SessionInfo session
    ) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", id)));

        if (!session.admin() && !Objects.equals(order.getClient().getUserId(), session.userId())) {
            throw new ForbiddenException();
        }

        if (!session.admin() && (order.getStatus() != OrderStatus.DRAFT || newStatus != OrderStatus.REVISION)) {
            throw new ValidationException("invalid_order_status", "status [" + order.getStatus() + "] cannot be changed to [" + newStatus + "]");
        }

        final OrderStatus previousStatus = order.getStatus();

        order.addSystemNote(String.format("usuario [%s] de tipo [%s] cambió estado de solicitud de [%s] a [%s]", session.userId(),
                session.getUserType().getTranslation(), previousStatus.getTranslation(), newStatus.getTranslation()));

        order.updateStatus(newStatus);

        Order updatedOrder = orderRepo.save(order);

        OrderResponse orderResponse = createOrderResponse(updatedOrder);

        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/{id}/returned/{returned}")
    public ResponseEntity<?> setReturned(@PathVariable("id") long id,
                                         @PathVariable("returned") boolean returned,
                                         @Session SessionInfo session) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        final Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", id)));

        order.setReturned(returned);

        orderRepo.save(order);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/billed/{billed}")
    public ResponseEntity<?> setBilled(@PathVariable("id") long id,
                                       @PathVariable("billed") boolean billed,
                                       @Session SessionInfo session) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        final Order order = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", id)));

        order.setBilled(billed);

        orderRepo.save(order);

        return ResponseEntity.noContent().build();
    }
}
