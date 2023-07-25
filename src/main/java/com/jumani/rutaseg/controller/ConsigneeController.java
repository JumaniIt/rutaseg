package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Consignee;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.service.auth.JwtService;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.client.ClientRepository;
import jakarta.transaction.Transactional;
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
@Transactional
public class ConsigneeController {

    private final ClientRepository clientRepository;
    private final JwtService jwtService;

    @Autowired
    public ConsigneeController(ClientRepository clientRepository, JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Consignee> createConsignee(@PathVariable("clientId") Long clientId,
                                                     @Session SessionInfo session,
                                                     @Valid @RequestBody Consignee consignee,
                                                     String token) {
        Client client = clientRepository.findById(clientId)
                .filter(c -> clientMatchesToken(c, session, token))
                .orElseThrow(() -> new ValidationException("client_not_found", "client not found"));

        Consignee newConsignee = new Consignee(consignee.getName(), consignee.getCuit());
        client.addConsignee(newConsignee);
        clientRepository.save(client);

        return ResponseEntity.status(HttpStatus.CREATED).body(newConsignee);
    }
    private boolean clientMatchesToken(Client client, SessionInfo session, String token) {
        boolean tokenValid = jwtService.isTokenValid(token) && jwtService.extractSubject(token).equals(client.getUserId().toString());
        return (session.admin() || (tokenValid && session.id() == client.getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<Consignee>> getAllConsignees(@PathVariable("clientId") Long clientId,
                                                            @Session SessionInfo session, String token){
        Client client = clientRepository.findById(clientId)
                .filter(c -> clientMatchesToken(c, session, token))
                .orElseThrow(() -> new ValidationException("client_not_found", "client not found"));

        List<Consignee> consignees = client.getConsignees();

        return new ResponseEntity<>(consignees, HttpStatus.OK);
    }
}