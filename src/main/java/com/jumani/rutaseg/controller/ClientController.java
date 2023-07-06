package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.dto.request.ClientRequest;
import com.jumani.rutaseg.dto.response.ClientResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.ClientRepository;
import com.jumani.rutaseg.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Transactional
@RequestMapping("/clients")
public class ClientController {

    private final UserRepository userRepo;
    private final ClientRepository clientRepo;

    public ClientController(UserRepository userRepo, ClientRepository clientRepo) {
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid ClientRequest request,
                                                 @Session SessionInfo session) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        User user = null;
        final Long userId = request.getUserId();
        if (Objects.nonNull(userId)) {
            user = userRepo.findById(userId).orElseThrow(() ->
                    new ValidationException("user_not_found", String.format("user with id [%s] not found", userId))
            );

            if (clientRepo.findOneByUser_Id(userId).isPresent()) {
                throw new ValidationException("user_already_taken", "a client with the same user already exists");
            }
        }

        final Client client = new Client(user, request.getPhone(), request.getCuit());
        final Client savedClient = clientRepo.save(client);

        final ClientResponse clientResponse = this.createResponse(savedClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientResponse);
    }

    private ClientResponse createResponse(Client client) {
        return new ClientResponse(client.getId(), client.getUserId(), client.getPhone(), client.getCUIT(), client.getConsignees());
    }
}
