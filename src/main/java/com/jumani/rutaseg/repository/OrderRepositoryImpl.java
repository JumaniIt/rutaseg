package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
@FieldNameConstants
public class OrderRepositoryImpl implements OrderRepositoryExtended {

    private final EntityManager entityManager;

    private final ContainerRepository containerRepo;
    private final FreeLoadRepository freeLoadRepo;


    public List<Order> search(
            @Nullable String codeLike,
            @Nullable Boolean pema,
            @Nullable Boolean transport,
            @Nullable Boolean port,
            @Nullable LocalDate arrivalDateFrom,
            @Nullable LocalDate arrivalDateTo,
            @Nullable LocalTime arrivalTimeFrom,
            @Nullable LocalTime arrivalTimeTo,
            @Nullable Long clientId,
            @Nullable OrderStatus status,
            @Nullable String loadCode,
            @Nullable String origin,
            @Nullable String target,
            @Nullable String consigneeCuit,
            @Nullable String destinationCode,
            List<Sort> sorts, // Agrega esta línea
            int offset,
            int limit
    ) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Order> criteriaQuery = builder.createQuery(Order.class);
        final Root<Order> root = criteriaQuery.from(Order.class);

        root.join(Order.Fields.client, JoinType.INNER);

        criteriaQuery.select(root);

        Predicate[] predicates = createPredicates(builder, root, codeLike, pema, transport, port, arrivalDateFrom,
                arrivalDateTo, arrivalTimeFrom, arrivalTimeTo, clientId, status, loadCode, origin, target,
                consigneeCuit, destinationCode);
        criteriaQuery.where(predicates);

        final List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
        for (Sort sort : sorts) {
            if (sort.field().equals("date")) {
                if (sort.ascending()) {
                    orders.add(builder.asc(root.get(Order.Fields.arrivalDate)));
                } else {
                    orders.add(builder.desc(root.get(Order.Fields.arrivalDate)));
                }

            } else if (sort.field().equals("load_code")) {
                final Path<Object> containerCode = root.join("containers", JoinType.LEFT).get("code");
                final Path<Object> freeLoadPatent = root.join("freeLoads", JoinType.LEFT).get("patent");
                if (sort.ascending()) {
                    orders.add(builder.asc(containerCode));
                    orders.add(builder.asc(freeLoadPatent));
                } else {
                    orders.add(builder.desc(containerCode));
                    orders.add(builder.desc(freeLoadPatent));
                }
            }
        }
        criteriaQuery.orderBy(orders);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long count(@Nullable String codeLike,
                      @Nullable Boolean pema,
                      @Nullable Boolean transport,
                      @Nullable Boolean port,
                      @Nullable LocalDate arrivalDateFrom,
                      @Nullable LocalDate arrivalDateTo,
                      @Nullable LocalTime arrivalTimeFrom,
                      @Nullable LocalTime arrivalTimeTo,
                      @Nullable Long clientId,
                      @Nullable OrderStatus status,
                      @Nullable String loadCode,
                      @Nullable String origin,
                      @Nullable String target,
                      @Nullable String consigneeCuit,
                      @Nullable String destinationCode) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<Order> root = criteriaQuery.from(Order.class);

        root.join(Order.Fields.client, JoinType.INNER);

        criteriaQuery.select(builder.count(root));

        Predicate[] predicates = createPredicates(builder, root, codeLike, pema, transport, port, arrivalDateFrom,
                arrivalDateTo, arrivalTimeFrom, arrivalTimeTo, clientId, status, loadCode, origin, target,
                consigneeCuit, destinationCode);
        criteriaQuery.where(predicates);

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private Predicate[] createPredicates(CriteriaBuilder builder, Root<Order> root,
                                         @Nullable String codeLike,
                                         @Nullable Boolean pema,
                                         @Nullable Boolean transport,
                                         @Nullable Boolean port,
                                         @Nullable LocalDate arrivalDateFrom,
                                         @Nullable LocalDate arrivalDateTo,
                                         @Nullable LocalTime arrivalTimeFrom,
                                         @Nullable LocalTime arrivalTimeTo,
                                         @Nullable Long clientId,
                                         @Nullable OrderStatus status,
                                         @Nullable String loadCode,
                                         @Nullable String origin,
                                         @Nullable String target,
                                         @Nullable String consigneeCuit,
                                         @Nullable String destinationCode) {

        final List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(codeLike)) {
            predicates.add(builder.like(root.get(Order.Fields.code), "%" + codeLike + "%"));
        }

        if (Objects.nonNull(pema)) {
            predicates.add(builder.equal(root.get(Order.Fields.pema), pema));
        }

        if (Objects.nonNull(transport)) {
            predicates.add(builder.equal(root.get(Order.Fields.transport), transport));
        }

        if (Objects.nonNull(port)) {
            predicates.add(builder.equal(root.get(Order.Fields.port), port));
        }

        if (Objects.nonNull(arrivalDateFrom)) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(Order.Fields.arrivalDate), arrivalDateFrom));
        }

        if (Objects.nonNull(arrivalDateTo)) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Order.Fields.arrivalDate), arrivalDateTo));
        }

        if (Objects.nonNull(arrivalTimeFrom)) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(Order.Fields.arrivalTime), arrivalTimeFrom));
        }

        if (Objects.nonNull(arrivalTimeTo)) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Order.Fields.arrivalTime), arrivalTimeTo));
        }

        if (Objects.nonNull(clientId)) {
            predicates.add(builder.equal(root.get(Order.Fields.client).get(Client.Fields.id), clientId));
        }

        if (Objects.nonNull(status)) {
            predicates.add(builder.equal(root.get(Order.Fields.status), status));
        }

        if (Objects.nonNull(origin)) {
            predicates.add(builder.equal(root.get(Order.Fields.origin), origin));
        }

        if (Objects.nonNull(target)) {
            predicates.add(builder.equal(root.get(Order.Fields.target), target));
        }

        if (Objects.nonNull(consigneeCuit)) {
            predicates.add(builder.equal(root.get(Order.Fields.consignee).get(ConsigneeData.Fields.cuit), consigneeCuit));
        }

        final List<Long> orderIds = new ArrayList<>();

        if (Objects.nonNull(loadCode)) {
            orderIds.addAll(containerRepo.findByCode(loadCode).stream().map(Container::getOrderId).toList());
            orderIds.addAll(freeLoadRepo.findByPatent(loadCode).stream().map(FreeLoad::getOrderId).toList());
        }

        if (Objects.nonNull(destinationCode)) {
            orderIds.addAll(containerRepo.findOrderIdByDestinationCode(destinationCode));
            orderIds.addAll(freeLoadRepo.findOrderIdByDestinationCode(destinationCode));
        }

        if (!orderIds.isEmpty()) {
            predicates.add(root.get(Order.Fields.id).in(orderIds.stream().distinct().collect(Collectors.toList())));
        }

        return predicates.toArray(new Predicate[0]);
    }
}