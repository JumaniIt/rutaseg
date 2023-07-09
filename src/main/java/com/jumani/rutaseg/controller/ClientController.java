package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.Consignee;
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
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
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

        final Client client = new Client(user, request.getName(), request.getPhone(), request.getCuit());
        final Client savedClient = clientRepo.save(client);

        final ClientResponse clientResponse = this.createResponse(savedClient, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientResponse);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> search(@RequestParam(value = "user_id", required = false) Long userId,
                                                       @RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "phone", required = false) String phone,
                                                       @RequestParam(value = "cuit", required = false) Long cuit,
                                                       @RequestParam(value = "page_size", required = false, defaultValue = "1") int pageSize,
                                                       @RequestParam(value = "with_consignees", required = false, defaultValue = "false") boolean withConsignees,
                                                       @Session SessionInfo session) {

        final Long theUserId;
        if (session.admin()) {
            theUserId = userId;
        } else {
            theUserId = session.id();
        }

        final int thePageSize;
        if (session.admin()) {
            thePageSize = pageSize;
        } else {
            thePageSize = 1;
        }

        final List<Client> clients = clientRepo.search(theUserId, name, phone, cuit, thePageSize);

        final List<ClientResponse> responses = clients.stream()
                .map(client -> this.createResponse(client, withConsignees))
                .toList();

        return ResponseEntity.ok(responses);
    }

    private ClientResponse createResponse(Client client, boolean withConsignees) {
        final List<Consignee> consignees = withConsignees ? client.getConsignees() : Collections.emptyList();
        return new ClientResponse(client.getId(), client.getUserId(), client.getName(), client.getPhone(), client.getCuit(), consignees);
    }
}
