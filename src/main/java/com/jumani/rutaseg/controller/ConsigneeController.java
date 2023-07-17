package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Consignee;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.repository.ConsigneeRepository;
import com.jumani.rutaseg.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/consignees")
@Validated
public class ConsigneeController {

    private final UserRepository userRepository;
    private final ConsigneeRepository consigneeRepository;

    @Autowired
    public ConsigneeController(UserRepository userRepository,
                               ConsigneeRepository consigneeRepository) {
        this.userRepository = userRepository;
        this.consigneeRepository = consigneeRepository;
    }

    @PostMapping
    public ResponseEntity<Consignee> createConsignee(@PathVariable("userId") Long userId,
                                                     @Valid @RequestBody Consignee consignee,
                                                     @RequestHeader("Authorization") String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("USER_NOT_FOUND", "User not found"));

        if (!user.isAdmin() && !userMatchesToken(user, token)) {
            throw new ValidationException("ACCESS_DENIED", "Access denied");
        }

        if (consignedExistsForUser(user, consignee.getCuit())) {
            throw new ValidationException("DUPLICATE_CONSIGNEE", "A consignee with the same CUIT already exists");
        }

        Consignee newConsignee = new Consignee(consignee.getName(), consignee.getCuit());
        consigneeRepository.save(newConsignee);

        return new ResponseEntity<>(newConsignee, HttpStatus.OK);
    }

    private boolean userMatchesToken(User user, String token) {
        return true;
    }

    private boolean consignedExistsForUser(User user, long cuit) {
        return consigneeRepository.existsByUserAndCuit(user, cuit);
    }

    @GetMapping
    public ResponseEntity<List<Consignee>> getAllConsignees(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("USER_NOT_FOUND", "User not found"));

        List<Consignee> consignees = consigneeRepository.findAllByUser(user);

        return new ResponseEntity<>(consignees, HttpStatus.OK);
    }
}