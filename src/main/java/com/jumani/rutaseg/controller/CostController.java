package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Cost;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.dto.request.CostRequest;
import com.jumani.rutaseg.dto.response.CostResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orders/{orderId}/costs")
public class CostController {

    private final OrderRepository orderRepo;

    public CostController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @PostMapping
    public ResponseEntity<CostResponse> createCost(
            @PathVariable("orderId") long orderId,
            @RequestBody @Valid CostRequest costRequest,
            @Session SessionInfo session
    ) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        Cost newCost = new Cost(
                costRequest.getAmount(),
                costRequest.getDescription(),
                costRequest.getType(),
                session.userId()
        );

        order.updateCost(newCost);
        orderRepo.save(order);

        CostResponse costResponse = createCostResponse(newCost);

        return ResponseEntity.status(HttpStatus.CREATED).body(costResponse);
    }

    private CostResponse createCostResponse(Cost cost) {
        return new CostResponse(
                cost.getId(),
                cost.getAmount(),
                cost.getDescription(),
                cost.getType(),
                cost.getCreatedAt()
        );
    }

    @PutMapping("/{costId}")
    public ResponseEntity<CostResponse> updateCost(
            @PathVariable("orderId") long orderId,
            @PathVariable("costId") long costId,
            @RequestBody @Valid CostRequest costRequest,
            @Session SessionInfo session
    ) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        Optional<Cost> costOptional = order.findCost(costId);
        if (costOptional.isEmpty()) {
            throw new NotFoundException(String.format("cost with id [%s] not found in order [%s]", costId, orderId));
        }

        Cost existingCost = costOptional.get();

        existingCost.update(
                costRequest.getAmount(),
                costRequest.getDescription(),
                costRequest.getType()
        );

        orderRepo.save(order);

        CostResponse costResponse = createCostResponse(existingCost);

        return ResponseEntity.ok(costResponse);
    }

    @DeleteMapping("/{costId}")
    public ResponseEntity<Void> deleteCost(
            @PathVariable("orderId") long orderId,
            @PathVariable("costId") long costId,
            @Session SessionInfo session
    ) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException(String.format("order with id [%s] not found", orderId)));

        Optional<Cost> costToRemoveOptional = order.removeCost(costId);

        if (costToRemoveOptional.isPresent()) {
            orderRepo.save(order);
        }

        return ResponseEntity.noContent().build();
    }
}