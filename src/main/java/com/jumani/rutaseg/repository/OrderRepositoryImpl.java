package com.jumani.rutaseg.repository;

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

    @Override
    public List<Order> search(@Nullable Boolean pema,
                              @Nullable Boolean transport,
                              @Nullable Boolean port,
                              @Nullable LocalDate arrivalDateFrom,
                              @Nullable LocalDate arrivalDateTo,
                              @Nullable LocalTime arrivalTimeFrom,
                              @Nullable LocalTime arrivalTimeTo,
                              @Nullable Long clientId,
                              @Nullable OrderStatus status,
                              int pageSize) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Order> criteriaQuery = builder.createQuery(Order.class);
        final Root<Order> root = criteriaQuery.from(Order.class);

        criteriaQuery.select(root);
        criteriaQuery.where(this.createPredicates(builder, root, pema, transport, port, arrivalDateFrom, arrivalDateTo, arrivalTimeFrom, arrivalTimeTo, clientId, status));
        criteriaQuery.orderBy(builder.asc(root.get(Order.Fields.id)));

        return entityManager.createQuery(criteriaQuery)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public long count(@Nullable Boolean pema, @Nullable Boolean transport, @Nullable Boolean port,
                      @Nullable LocalDate arrivalDateFrom, @Nullable LocalDate arrivalDateTo,
                      @Nullable LocalTime arrivalTimeFrom, @Nullable LocalTime arrivalTimeTo,
                      @Nullable Long clientId, @Nullable OrderStatus status) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<Order> root = criteriaQuery.from(Order.class);

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(createPredicates(builder, root, pema, transport, port, arrivalDateFrom,
                arrivalDateTo, arrivalTimeFrom, arrivalTimeTo, clientId, status));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private Predicate[] createPredicates(CriteriaBuilder builder, Root<Order> root,
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
            predicates.add(builder.greaterThanOrEqualTo(root.get(Order.Fields.arrivalData).get("arrivalDate"), arrivalDateFrom));
        }

        if (Objects.nonNull(arrivalDateTo)) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Order.Fields.arrivalData).get("arrivalDate"), arrivalDateTo));
        }

        if (Objects.nonNull(arrivalTimeFrom)) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(Order.Fields.arrivalData).get("arrivalTime"), arrivalTimeFrom));
        }

        if (Objects.nonNull(arrivalTimeTo)) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Order.Fields.arrivalData).get("arrivalTime"), arrivalTimeTo));
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