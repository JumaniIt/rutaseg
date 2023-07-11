package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.OrderStatus;
import com.jumani.rutaseg.dto.request.ArrivalDataRequest;
import com.jumani.rutaseg.dto.request.CustomsDataRequest;
import com.jumani.rutaseg.dto.request.DriverDataRequest;
import com.jumani.rutaseg.dto.request.OrderRequest;
import com.jumani.rutaseg.dto.response.OrderResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.repository.ClientRepository;
import com.jumani.rutaseg.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void createOrder_WithValidData_ReturnsOrderResponse() {
        // Arrange
        boolean pema = TestDataGen.randomBoolean();
        boolean port = TestDataGen.randomBoolean();
        boolean transport = TestDataGen.randomBoolean();
        long createdByUserId = TestDataGen.randomId();
        long clientId = TestDataGen.randomId();


        ArrivalDataRequest arrivalDataRequest = new ArrivalDataRequest();
        DriverDataRequest driverDataRequest = new DriverDataRequest();
        CustomsDataRequest customsDataRequest = new CustomsDataRequest();
        OrderRequest orderRequest = new OrderRequest(
                clientId, pema, port, transport, arrivalDataRequest, driverDataRequest, customsDataRequest
        );


        SessionInfo session = new SessionInfo(createdByUserId, true);

        Client client = mock(Client.class);
        Order savedOrder = mock(Order.class);

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(client));
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        when(savedOrder.getId()).thenReturn(1L);
        when(savedOrder.isPema()).thenReturn(pema);
        when(savedOrder.isPort()).thenReturn(port);
        when(savedOrder.isTransport()).thenReturn(transport);
        when(savedOrder.getStatus()).thenReturn(OrderStatus.DRAFT);
        when(savedOrder.getCreatedAt()).thenReturn(ZonedDateTime.now());
        when(savedOrder.getFinishedAt()).thenReturn(null);

        OrderResponse expectedOrderResponse = new OrderResponse(
                1L, pema, port, transport, OrderStatus.DRAFT,
                ZonedDateTime.now(), null, null, null, null
        );

        // Act
        ResponseEntity<OrderResponse> response = controller.createOrder(orderRequest, session);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedOrderResponse, response.getBody());

        verify(clientRepo).findById(clientId);
        verify(orderRepo).save(any(Order.class));
        verifyNoMoreInteractions(clientRepo, orderRepo);
    }

    @Test
    void createOrder_WithNonAdminUserAndDifferentUserId_ThrowsForbiddenException() {
        // Arrange
        boolean pema = TestDataGen.randomBoolean();
        boolean port = TestDataGen.randomBoolean();
        boolean transport = TestDataGen.randomBoolean();
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
        boolean pema = TestDataGen.randomBoolean();
        boolean port = TestDataGen.randomBoolean();
        boolean transport = TestDataGen.randomBoolean();
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

