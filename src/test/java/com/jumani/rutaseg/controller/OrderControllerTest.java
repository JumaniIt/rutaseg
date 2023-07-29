package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.domain.*;
import com.jumani.rutaseg.dto.request.ArrivalDataRequest;
import com.jumani.rutaseg.dto.request.CustomsDataRequest;
import com.jumani.rutaseg.dto.request.DriverDataRequest;
import com.jumani.rutaseg.dto.request.OrderRequest;
import com.jumani.rutaseg.dto.response.*;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.repository.OrderRepository;
import com.jumani.rutaseg.repository.client.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    ClientRepository clientRepo;

    @Mock
    OrderRepository orderRepo;

    @InjectMocks
    OrderController controller;

    @Test
    public void getById_WithValidIdAndMatchingClient_ShouldReturnOrderResponse() {
        // Arrange
        long orderId = 1L;
        long clientId = 1L;
        long createdByUserId = 2L;
        boolean pema = true;
        boolean port = false;
        boolean transport = true;
        OrderStatus status = OrderStatus.DRAFT;
        ZonedDateTime createdAt = ZonedDateTime.now().minusDays(1);
        ZonedDateTime finishedAt = ZonedDateTime.now();


        OrderResponse expectedResponse = new OrderResponse(
                orderId,
                clientId,
                createdByUserId,
                pema,
                port,
                transport,
                status,
                createdAt,
                finishedAt,
                null,
                null,
                null
        );

        Order order = mock(Order.class);
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getId()).thenReturn(orderId);
        when(order.getClientId()).thenReturn(clientId);
        when(order.getCreatedByUserId()).thenReturn(createdByUserId);
        when(order.isPema()).thenReturn(pema);
        when(order.isPort()).thenReturn(port);
        when(order.isTransport()).thenReturn(transport);
        when(order.getStatus()).thenReturn(status);
        when(order.getCreatedAt()).thenReturn(createdAt);
        when(order.getFinishedAt()).thenReturn(finishedAt);
        SessionInfo sessionInfo = new SessionInfo(1L, true);

        // Act
        ResponseEntity<OrderResponse> response = controller.getById(orderId, sessionInfo);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrderResponse actualResponse = response.getBody();

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getClientId(), actualResponse.getClientId());
        assertEquals(expectedResponse.getCreatedByUserId(), actualResponse.getCreatedByUserId());
        assertEquals(expectedResponse.isPema(), actualResponse.isPema());
        assertEquals(expectedResponse.isPort(), actualResponse.isPort());
        assertEquals(expectedResponse.isTransport(), actualResponse.isTransport());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getFinishedAt(), actualResponse.getFinishedAt());
        assertEquals(expectedResponse.getArrivalData(), actualResponse.getArrivalData());
        assertEquals(expectedResponse.getDriverData(), actualResponse.getDriverData());
        assertEquals(expectedResponse.getCustomsData(), actualResponse.getCustomsData());
    }
    @Test
    public void getById_NonAdminSessionAndDifferentClientIds_ThrowsNotFoundException() {
        // Arrange
        long orderId = 1L;
        long clientId = 2L;
        long sessionClientId = 3L;
        SessionInfo session = new SessionInfo(sessionClientId, false);

        Order order = mock(Order.class);
        Client client = mock(Client.class);

        when(order.getClient()).thenReturn(client);
        when(client.getUserId()).thenReturn(clientId);
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        OrderController orderController = new OrderController(orderRepo, clientRepo);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            orderController.getById(orderId, session);
        });

        // Verify
        verify(orderRepo, times(1)).findById(orderId);
    }


    @Test
    void createOrder_WithValidData_ReturnsOrderResponse() {
        // Arrange
        boolean pema = TestDataGen.randomBoolean();
        boolean port = TestDataGen.randomBoolean();
        boolean transport = TestDataGen.randomBoolean();
        long clientId = TestDataGen.randomId();

        ArrivalDataRequest arrivalDataRequest = new ArrivalDataRequest();
        DriverDataRequest driverDataRequest = new DriverDataRequest();
        CustomsDataRequest customsDataRequest = new CustomsDataRequest();
        OrderRequest orderRequest = new OrderRequest(
                clientId, pema, port, transport, arrivalDataRequest, driverDataRequest, customsDataRequest,null,null
        );

        SessionInfo session = new SessionInfo(TestDataGen.randomId(), true);

        Client client = mock(Client.class);

        Order savedOrder = mock(Order.class);
        when(savedOrder.getClientId()).thenReturn(clientId);
        long createdByUserId = TestDataGen.randomId(); // Generar un número aleatorio para createdByUserId
        when(savedOrder.getCreatedByUserId()).thenReturn(createdByUserId); // Configurar el mock para devolver el número aleatorio

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);
        when(savedOrder.getClientId()).thenReturn(clientId);

        when(savedOrder.getId()).thenReturn(1L);
        when(savedOrder.isPema()).thenReturn(pema);
        when(savedOrder.isPort()).thenReturn(port);
        when(savedOrder.isTransport()).thenReturn(transport);
        when(savedOrder.getStatus()).thenReturn(OrderStatus.DRAFT);
        when(savedOrder.getCreatedAt()).thenReturn(ZonedDateTime.now());
        when(savedOrder.getFinishedAt()).thenReturn(null);

        OrderResponse expectedOrderResponse = new OrderResponse(
                1L, clientId, createdByUserId, pema, port, transport, OrderStatus.DRAFT,
                ZonedDateTime.now(), null, null, null, null
        );

        // Act
        ResponseEntity<OrderResponse> response = controller.createOrder(orderRequest, session);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        OrderResponse actualOrderResponse = response.getBody();

        assertEquals(expectedOrderResponse, actualOrderResponse);

        verify(clientRepo).findById(clientId);
        verify(orderRepo).save(any(Order.class));
        verifyNoMoreInteractions(clientRepo, orderRepo);
    }

    @Test
    void createOrder_WithNonAdminUserAndDifferentUserId_ThrowsForbiddenException() {
        // Arrange
        long createdByUserId = TestDataGen.randomId();
        long clientId = TestDataGen.randomId();

        OrderRequest orderRequest = mock(OrderRequest.class);
        when(orderRequest.getClientId()).thenReturn(clientId);

        SessionInfo session = new SessionInfo(createdByUserId, false);

        Client client = mock(Client.class);

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));
        when(client.getUserId()).thenReturn(clientId + 1);

        // Act and Assert
        assertThrows(ForbiddenException.class, () -> {
            controller.createOrder(orderRequest, session);
        });

        verify(clientRepo).findById(clientId);
        verifyNoMoreInteractions(clientRepo, orderRepo);
    }

    @Test
    void createOrder_WithNonExistentClient_ThrowsNotFoundException() {
        // Arrange
        long createdByUserId = TestDataGen.randomId();
        long clientId = TestDataGen.randomId();

        OrderRequest orderRequest = mock(OrderRequest.class);
        when(orderRequest.getClientId()).thenReturn(clientId);

        SessionInfo session = new SessionInfo(createdByUserId, true);

        when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> {
            controller.createOrder(orderRequest, session);
        });

        verify(clientRepo).findById(clientId);
        verifyNoMoreInteractions(clientRepo, orderRepo);
    }
}

