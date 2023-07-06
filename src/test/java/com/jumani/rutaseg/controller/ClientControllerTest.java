package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.Consignee;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.dto.request.ClientRequest;
import com.jumani.rutaseg.dto.response.ClientResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.repository.ClientRepository;
import com.jumani.rutaseg.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jumani.rutaseg.TestDataGen.randomId;
import static com.jumani.rutaseg.TestDataGen.randomShortString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    ClientRepository clientRepo;
    @Mock
    UserRepository userRepo;

    @InjectMocks
    ClientController controller;

    @Test
    void create_WithUser_Ok() {
        final String phone = randomShortString();
        final long CUIT = randomId();
        final long userId = randomId();
        final List<Consignee> consignees = List.of(new Consignee(randomShortString(), randomId()));
        final long clientId = randomId();

        final SessionInfo session = new SessionInfo(1L, true);
        final ClientRequest request = new ClientRequest(phone, CUIT, userId);

        final User user = mock(User.class);
        final Client savedClient = mock(Client.class);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepo.findOneByUser_Id(userId)).thenReturn(Optional.empty());
        when(clientRepo.save(any(Client.class))).thenReturn(savedClient);

        when(savedClient.getId()).thenReturn(clientId);
        when(savedClient.getUserId()).thenReturn(userId);
        when(savedClient.getPhone()).thenReturn(phone);
        when(savedClient.getCUIT()).thenReturn(CUIT);
        when(savedClient.getConsignees()).thenReturn(consignees);

        final ClientResponse expectedClientResponse = new ClientResponse(clientId, userId, phone, CUIT, consignees);
        final ResponseEntity<ClientResponse> response = controller.create(request, session);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedClientResponse, response.getBody());

        verifyNoMoreInteractions(clientRepo, userRepo);
    }

    @Test
    void create_WithoutUser_Ok() {
        final String phone = randomShortString();
        final long CUIT = randomId();
        final List<Consignee> consignees = new ArrayList<>();
        final long clientId = randomId();

        final SessionInfo session = new SessionInfo(1L, true);
        final ClientRequest request = new ClientRequest(phone, CUIT, null);

        final Client savedClient = mock(Client.class);

        when(clientRepo.save(any(Client.class))).thenReturn(savedClient);

        when(savedClient.getId()).thenReturn(clientId);
        when(savedClient.getUserId()).thenReturn(null);
        when(savedClient.getPhone()).thenReturn(phone);
        when(savedClient.getCUIT()).thenReturn(CUIT);
        when(savedClient.getConsignees()).thenReturn(consignees);

        final ClientResponse expectedClientResponse = new ClientResponse(clientId, null, phone, CUIT, consignees);
        final ResponseEntity<ClientResponse> response = controller.create(request, session);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedClientResponse, response.getBody());

        verifyNoMoreInteractions(clientRepo, userRepo);
    }

    @Test
    void create_UserNotFound_ValidationException() {
        final String phone = randomShortString();
        final long CUIT = randomId();
        final long userId = randomId();

        final SessionInfo session = new SessionInfo(1L, true);
        final ClientRequest request = new ClientRequest(phone, CUIT, userId);

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        final ValidationException valEx = assertThrows(ValidationException.class, () -> controller.create(request, session));

        assertEquals("user_not_found", valEx.getCode());
        assertEquals(String.format("user with id [%s] not found", userId), valEx.getMessage());

        verifyNoInteractions(clientRepo);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void create_UserAlreadyTaken_ValidationException() {
        final String phone = randomShortString();
        final long CUIT = randomId();
        final long userId = randomId();

        final SessionInfo session = new SessionInfo(1L, true);
        final ClientRequest request = new ClientRequest(phone, CUIT, userId);

        final User user = mock(User.class);
        final Client existentClient = mock(Client.class);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepo.findOneByUser_Id(userId)).thenReturn(Optional.of(existentClient));

        final ValidationException valEx = assertThrows(ValidationException.class, () -> controller.create(request, session));

        assertEquals("user_already_taken", valEx.getCode());
        assertEquals("a client with the same user already exists", valEx.getMessage());

        verifyNoMoreInteractions(clientRepo, userRepo);
    }

    @Test
    void create_NotAdminSession_Forbidden() {
        final SessionInfo session = new SessionInfo(1L, false);

        final ClientRequest request = new ClientRequest();

        assertThrows(ForbiddenException.class, () -> controller.create(request, session));

        verifyNoInteractions(clientRepo, userRepo);
    }
}