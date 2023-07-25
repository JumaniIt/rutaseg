package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Consignee;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.dto.request.ConsigneeRequest;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.repository.client.ClientRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsigneeControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ConsigneeController consigneeController;

    @Test
    void createConsignee_Success() {
        Long clientId = 1L;
        SessionInfo sessionInfo = new SessionInfo(1L, true);
        ConsigneeRequest consigneeRequest = new ConsigneeRequest();
        consigneeRequest.setName("Name");
        consigneeRequest.setCuit(123456789);

        Client client = mock(Client.class);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        ResponseEntity<Consignee> response = consigneeController.createConsignee(clientId, sessionInfo, consigneeRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createConsignee_Failure_InvalidUser() {
        Long clientId = 16L;
        SessionInfo sessionInfo = new SessionInfo(16L, true);
        ConsigneeRequest consigneeRequest = new ConsigneeRequest();
        consigneeRequest.setName("Name");
        consigneeRequest.setCuit(123456789);

        Client client = mock(Client.class);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> consigneeController.createConsignee(clientId, sessionInfo, consigneeRequest));

        assertEquals("client_not_found", exception.getCode());
        assertEquals("client not found", exception.getMessage());
    }

    @Test
    void createConsignee_Failure_DuplicateCUIT() {
        Long clientId = 12L;
        SessionInfo sessionInfo = new SessionInfo(12L, true);
        ConsigneeRequest consigneeRequest = new ConsigneeRequest();
        consigneeRequest.setName("Name");
        consigneeRequest.setCuit(123456789);

        Client client = mock(Client.class);
        Consignee existingConsignee = new Consignee("existing Consignee", 123456789);
        client.addConsignee(existingConsignee);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> consigneeController.createConsignee(clientId, sessionInfo, consigneeRequest));

        assertEquals("duplicate_cuit", exception.getCode());
        assertEquals("a client with the same CUIT already exists", exception.getMessage());
    }
    @Test
    void getAllConsignees_Success() {
        Long clientId = 1L;
        SessionInfo sessionInfo = new SessionInfo(1L, true);

        Client client = mock(Client.class);
        Consignee consignee1 = new Consignee("Consignee 1", 123456789);
        Consignee consignee2 = new Consignee("Consignee 2", 123456799);
        client.addConsignee(consignee1);
        client.addConsignee(consignee2);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        ResponseEntity<List<Consignee>> response = consigneeController.getAllConsignees(clientId, sessionInfo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().containsAll(Arrays.asList(consignee1, consignee2)));
    }

    @Test
    void getAllConsignees_Failure_InvalidUser() {
        Long clientId = 16L;
        SessionInfo sessionInfo = new SessionInfo(16L, false);

        Client client = mock(Client.class);
        Consignee consignee = new Consignee("Consignee", 123456789);
        client.addConsignee(consignee);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> consigneeController.getAllConsignees(clientId, sessionInfo));

        assertEquals("client_not_found", exception.getCode());
        assertEquals("client not found", exception.getMessage());
    }
}
