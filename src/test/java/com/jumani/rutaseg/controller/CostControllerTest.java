package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.*;
import com.jumani.rutaseg.dto.request.CostRequest;
import com.jumani.rutaseg.dto.response.CostResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

public class CostControllerTest {

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private CostController costController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCost_HappyPath() {
        final long orderId = 1L;
        final SessionInfo session = new SessionInfo(1L, true);
        final CostRequest request = new CostRequest();
        request.setAmount(100.0);
        request.setType(CostType.PEMA);
        request.setDescription("Description");

        Order mockOrder = new Order("ABC123", null, false, false, false,
                null, null, null, 1L, null, null);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(mockOrder));

        ResponseEntity<CostResponse> response = costController.createCost(orderId, request, session);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        CostResponse costResponse = response.getBody();
        assertNotNull(costResponse);
        assertEquals(100.0, costResponse.getAmount());
        assertEquals(CostType.PEMA, costResponse.getType());
        assertEquals("Description", costResponse.getDescription());

        verify(orderRepo).findById(orderId);
        verify(orderRepo).save(mockOrder);
    }

    @Test
    void createCost_NotAdminSession_Forbidden() {
        final long orderId = 1L;
        final SessionInfo session = new SessionInfo(1L, false);
        final CostRequest request = new CostRequest();
        request.setAmount(100.0);
        request.setType(CostType.PEMA);
        request.setDescription("Description");

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> costController.createCost(orderId, request, session));

        verifyNoInteractions(orderRepo);
    }

    @Test
    void orderNotFound_NotFoundException() {
        final long orderId = 1L;
        final long costId = 2L;
        final SessionInfo session = new SessionInfo(1L, true);
        final CostRequest request = new CostRequest();
        request.setAmount(200.0);
        request.setType(CostType.PEMA);
        request.setDescription("Updated Description");

        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> costController.updateCost(orderId, costId, request, session));

        assertEquals("order with id [1] not found", exception.getMessage());

        verify(orderRepo).findById(orderId);
        verifyNoMoreInteractions(orderRepo);
    }

    @Test
    void createCost_NonAdminUser_Forbidden() {
        final long orderId = 1L;
        final SessionInfo session = new SessionInfo(1L, false);
        final CostRequest request = new CostRequest();
        request.setAmount(100.0);
        request.setType(CostType.PEMA);
        request.setDescription("description");

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> costController.createCost(orderId, request, session));

        verifyNoInteractions(orderRepo);
    }

    @Test
    void updateCost_HappyPath() {
        // Arrange
        final long orderId = 1L;
        final long costId = 2L;
        final SessionInfo session = new SessionInfo(1L, true);
        final CostRequest request = new CostRequest();
        request.setAmount(200.0);
        request.setType(CostType.PEMA);
        request.setDescription("updated Description");

        Order mockOrder = mock(Order.class);
        Cost existingCost = new Cost(100.0, "original Description", CostType.PEMA, 1L);
        existingCost.setId(costId);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(mockOrder.findCost(costId)).thenReturn(Optional.of(existingCost));
        when(orderRepo.save(mockOrder)).thenReturn(mockOrder); // Simula que se guarda correctamente

        ResponseEntity<CostResponse> response = costController.updateCost(orderId, costId, request, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CostResponse costResponse = response.getBody();
        assertNotNull(costResponse);
        assertEquals(200.0, costResponse.getAmount());
        assertEquals(CostType.PEMA, costResponse.getType());
        assertEquals("updated Description", costResponse.getDescription());

        // Verifica que el costo en el pedido también se actualice correctamente
        assertEquals(200.0, existingCost.getAmount());
        assertEquals(CostType.PEMA, existingCost.getType());
        assertEquals("updated Description", existingCost.getDescription());

        verify(orderRepo).findById(orderId);
        verify(mockOrder).findCost(costId);
        verify(orderRepo).save(mockOrder);
    }

    @Test
    void updateCost_OrderNotFound_NotFoundException() {
        final long orderId = 1L;
        final long costId = 2L;
        final SessionInfo session = new SessionInfo(1L, true);
        final CostRequest request = new CostRequest();
        request.setAmount(200.0);
        request.setType(CostType.PEMA);
        request.setDescription("Updated Description");

        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> costController.updateCost(orderId, costId, request, session));

        assertEquals("order with id [1] not found", exception.getMessage());

        verify(orderRepo).findById(orderId);
        verifyNoMoreInteractions(orderRepo);
    }

    @Test
    void costNotFound_NotFoundException() {
        final long orderId = 1L;
        final long costId = 2L;
        final SessionInfo session = new SessionInfo(1L, true);
        final CostRequest request = new CostRequest();
        request.setAmount(200.0);
        request.setType(CostType.PEMA);
        request.setDescription("Updated Description");

        Order mockOrder = mock(Order.class);
        when(mockOrder.findCost(costId)).thenReturn(Optional.empty());
        when(orderRepo.findById(orderId)).thenReturn(Optional.of(mockOrder));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> costController.updateCost(orderId, costId, request, session));

        assertEquals(String.format("cost with id [%s] not found in order [%s]", costId, orderId), exception.getMessage());

        verify(orderRepo).findById(orderId);
        verify(mockOrder).findCost(costId);
        verifyNoMoreInteractions(orderRepo);
        verifyNoMoreInteractions(mockOrder);
    }

    @Test
    void deleteCost_HappyPath() {
        final long orderId = 1L;
        final long costId = 2L;
        final SessionInfo session = new SessionInfo(1L, true);

        Order mockOrder = mock(Order.class);
        Cost existingCost = new Cost(100.0, "Description", CostType.PEMA, 1L);
        existingCost.setId(costId);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(mockOrder));
        doReturn(Optional.of(existingCost)).when(mockOrder).removeCost(costId);

        when(orderRepo.save(mockOrder)).thenReturn(mockOrder);
        ResponseEntity<Void> response = costController.deleteCost(orderId, costId, session);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(orderRepo).findById(orderId);
        verify(mockOrder).removeCost(costId);
        verify(orderRepo).save(mockOrder);
    }

    @Test
    void deleteCost_NonAdminUser_Forbidden() {
        final long orderId = 1L;
        final long costId = 2L;
        final SessionInfo session = new SessionInfo(1L, false);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> costController.deleteCost(orderId, costId, session));

        assertEquals("insufficient privileges to access this resource", exception.getMessage());
        verifyNoInteractions(orderRepo);
    }

}
