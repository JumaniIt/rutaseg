package com.jumani.rutaseg.controller;

import com.jumani.rutaseg.domain.Cost;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.dto.request.CostRequest;
import com.jumani.rutaseg.dto.response.CostResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.CostRepository;
import com.jumani.rutaseg.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders/{id}/costs")
public class CostController {

    private final OrderRepository orderRepo;
    private final CostRepository costRepo;

    public CostController(OrderRepository orderRepo, CostRepository costRepo) {
        this.orderRepo = orderRepo;
        this.costRepo = costRepo;
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
                .orElseThrow(() -> new NotFoundException("order not found"));

        Cost newCost = new Cost(
                costRequest.getAmount(),
                costRequest.getDescription(),
                costRequest.getType(),
                order
        );

        Cost savedCost = costRepo.save(newCost);

        order.updateCost(savedCost);
        orderRepo.save(order);

        CostResponse costResponse = createCostResponse(savedCost);

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
            @PathVariable("id") long orderId,
            @PathVariable("costId") long costId,
            @RequestBody @Valid CostRequest costRequest,
            @Session SessionInfo session
    ) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order not found"));

        Optional<Cost> costOptional = costRepo.findById(costId);
        if (costOptional.isEmpty()) {
            throw new NotFoundException("cost not found");
        }

        Cost existingCost = costOptional.get();

        existingCost.setAmount(costRequest.getAmount());
        existingCost.setDescription(costRequest.getDescription());
        existingCost.setType(costRequest.getType());

        Cost updatedCost = costRepo.save(existingCost);

        order.updateCost(updatedCost);
        orderRepo.save(order);

        CostResponse costResponse = createCostResponse(updatedCost);

        return ResponseEntity.ok(costResponse);
    }

    @DeleteMapping("/{costId}")
    public ResponseEntity<Void> deleteCost(
            @PathVariable("id") long orderId,
            @PathVariable("costId") long costId,
            @Session SessionInfo session
    ) {
        if (!session.admin()) {
            throw new ForbiddenException();
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order not found"));

        Optional<Cost> costToRemoveOptional = order.removeCost(costId);

        if (costToRemoveOptional.isPresent()) {
            orderRepo.save(order);
        }

        return ResponseEntity.noContent().build();
    }
}