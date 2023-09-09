package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.ArrivalData;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
@FieldNameConstants
public class OrderRepositoryImpl implements OrderRepositoryExtended {

    private final EntityManager entityManager;

    public List<Order> search(@Nullable String codeLike,
                              @Nullable Boolean pema,
                              @Nullable Boolean transport,
                              @Nullable Boolean port,
                              @Nullable LocalDate arrivalDateFrom,
                              @Nullable LocalDate arrivalDateTo,
                              @Nullable LocalTime arrivalTimeFrom,
                              @Nullable LocalTime arrivalTimeTo,
                              @Nullable Long clientId,
                              @Nullable OrderStatus status,
                              int offset,
                              int limit) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Order> criteriaQuery = builder.createQuery(Order.class);
        final Root<Order> root = criteriaQuery.from(Order.class);

        criteriaQuery.select(root);
        criteriaQuery.where(createPredicates(builder, root, codeLike, pema, transport, port, arrivalDateFrom,
                arrivalDateTo, arrivalTimeFrom, arrivalTimeTo, clientId, status));
        criteriaQuery.orderBy(builder.asc(root.get(Order.Fields.id)));

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
                      @Nullable OrderStatus status) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<Order> root = criteriaQuery.from(Order.class);

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(createPredicates(builder, root, codeLike, pema, transport, port, arrivalDateFrom,
                arrivalDateTo, arrivalTimeFrom, arrivalTimeTo, clientId, status));

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
                                         @Nullable OrderStatus status) {

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
            predicates.add(builder.greaterThanOrEqualTo(root.get(Order.Fields.arrivalData).get(ArrivalData.Fields.arrivalDate), arrivalDateFrom));
        }

        if (Objects.nonNull(arrivalDateTo)) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Order.Fields.arrivalData).get(ArrivalData.Fields.arrivalDate), arrivalDateTo));
        }

        if (Objects.nonNull(arrivalTimeFrom)) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(Order.Fields.arrivalData).get(ArrivalData.Fields.arrivalTime), arrivalTimeFrom));
        }

        if (Objects.nonNull(arrivalTimeTo)) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Order.Fields.arrivalData).get(ArrivalData.Fields.arrivalTime), arrivalTimeTo));
        }

        if (Objects.nonNull(clientId)) {
            predicates.add(builder.equal(root.get(Order.Fields.client).get(Client.Fields.id), clientId));
        }

        if (Objects.nonNull(status)) {
            predicates.add(builder.equal(root.get(Order.Fields.status), status));
        }

        return predicates.toArray(new Predicate[0]);
    }
}