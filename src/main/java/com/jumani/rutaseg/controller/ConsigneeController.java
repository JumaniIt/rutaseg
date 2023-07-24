package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Consignee;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.client.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/consignees")
@Validated
public class ConsigneeController {

    private final ClientRepository clientRepository;

    @Autowired
    public ConsigneeController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<Consignee> createConsignee(@PathVariable("clientId") Long clientId,
                                                     @Session SessionInfo session,
                                                     @Valid @RequestBody Consignee consignee,
                                                     @RequestHeader("Authorization") String token) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ValidationException("CLIENT_NOT_FOUND", "Client not found"));

        if (!session.admin() && !clientMatchesToken(client, token)) {
            throw new ValidationException("ACCESS_DENIED", "Access denied");
        }

        if (consignedExistsForClient(client, consignee.getCuit())) {
            throw new ValidationException("DUPLICATE_CONSIGNEE", "A consignee with the same CUIT already exists");
        }

        Consignee newConsignee = new Consignee(consignee.getName(), consignee.getCuit());
        client.addConsignee(newConsignee);
        clientRepository.save(client);

        return new ResponseEntity<>(newConsignee, HttpStatus.OK);
    }

    private boolean clientMatchesToken(Client client, String token) {
        return true;
    }

    private boolean consignedExistsForClient(Client client, long cuit) {
        List<Consignee> consignees = client.getConsignees();

        for (Consignee consignee : consignees) {
            if (consignee.getCuit() == cuit) {
                return true;
            }
        }
        return false;
    }

    @GetMapping
    public ResponseEntity<List<Consignee>> getAllConsignees(@PathVariable("clientId") Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ValidationException("CLIENT_NOT_FOUND", "Client not found"));

        List<Consignee> consignees = client.getConsignees();

        return new ResponseEntity<>(consignees, HttpStatus.OK);
    }
}