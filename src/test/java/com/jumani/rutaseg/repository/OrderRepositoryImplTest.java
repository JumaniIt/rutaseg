package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.IntegrationTest;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.Container;
import com.jumani.rutaseg.domain.ContainerType;
import com.jumani.rutaseg.domain.Destination;
import com.jumani.rutaseg.domain.DestinationType;
import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.OrderStatus;
import com.jumani.rutaseg.domain.Sort;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.repository.client.ClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class OrderRepositoryImplTest extends IntegrationTest {

    private static final List<Sort> SORT_BY_ID_ASC = List.of(new Sort("creation_date", true));

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ClientRepository clientRepo;

    @Autowired
    OrderRepository orderRepo;

    // ---------- search ----------

    @Test
    void search_byCodeSubstring_returnsMatch() {
        final Client client = clientForUser("u1");

        final Order o1 = saveBareOrder(client, "CODE-001", OrderStatus.DRAFT);
        final Order o2 = saveBareOrder(client, "CODE-002", OrderStatus.DRAFT);
        saveBareOrder(client, "OTRO-003", OrderStatus.DRAFT);

        final List<Order> result = orderRepo.search(
                "CODE", null, null, null,
                null, null, null, null, null, null,
                client.getId(), null, null, null, null, null, null,
                SORT_BY_ID_ASC, 0, 10);

        assertEquals(List.of(o1, o2), result);
        assertEquals(2L, orderRepo.count(
                "CODE", null, null, null,
                null, null, null, null, null, null,
                client.getId(), null, null, null, null, null, null));
    }

    @Test
    void search_byStatus_filtersByStatus() {
        final Client client = clientForUser("u1");

        final Order draft = saveBareOrder(client, "C1", OrderStatus.DRAFT);
        saveBareOrder(client, "C2", OrderStatus.PROCESSING);
        saveBareOrder(client, "C3", OrderStatus.CANCELLED);

        final List<Order> result = orderRepo.search(
                null, null, null, null,
                null, null, null, null, null, null,
                client.getId(), OrderStatus.DRAFT,
                null, null, null, null, null,
                SORT_BY_ID_ASC, 0, 10);

        assertEquals(List.of(draft), result);
    }

    @Test
    void search_byClientId_isolatesPerClient() {
        final Client clientA = clientForUser("ua");
        final Client clientB = clientForUser("ub");

        final Order forA = saveBareOrder(clientA, "A-1", OrderStatus.DRAFT);
        saveBareOrder(clientB, "B-1", OrderStatus.DRAFT);
        saveBareOrder(clientB, "B-2", OrderStatus.DRAFT);

        assertEquals(List.of(forA), orderRepo.search(
                null, null, null, null,
                null, null, null, null, null, null,
                clientA.getId(), null, null, null, null, null, null,
                SORT_BY_ID_ASC, 0, 10));
        assertEquals(2L, orderRepo.count(
                null, null, null, null,
                null, null, null, null, null, null,
                clientB.getId(), null, null, null, null, null, null));
    }

    @Test
    void search_byArrivalDateRange_includesBoundaries() {
        final Client client = clientForUser("u1");

        final Order onMin = saveOrderWithArrivalDate(client, "C1", LocalDate.of(2025, 1, 10));
        final Order inside = saveOrderWithArrivalDate(client, "C2", LocalDate.of(2025, 1, 15));
        final Order onMax = saveOrderWithArrivalDate(client, "C3", LocalDate.of(2025, 1, 20));
        saveOrderWithArrivalDate(client, "C4", LocalDate.of(2025, 1, 25));

        final List<Order> result = orderRepo.search(
                null, null, null, null,
                null, null,
                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20),
                null, null,
                client.getId(), null, null, null, null, null, null,
                SORT_BY_ID_ASC, 0, 10);

        assertEquals(List.of(onMin, inside, onMax), result);
    }

    /**
     * Migration regression test (MySQL → PostgreSQL). Asserts that searching by code is
     * case-insensitive — MySQL's default {@code utf8mb4_general_ci} collation provides this for free,
     * and H2 in {@code MODE=MySQL} replicates it. PostgreSQL's {@code LIKE} is case-sensitive, so
     * the migration must wrap {@code codeLike} in {@code cb.lower(...)} ({@code OrderRepositoryImpl.java:170})
     * to keep this test green after the cutover.
     */
    @Test
    void search_byCodeMixedCase_isCaseInsensitive() {
        final Client client = clientForUser("u1");
        final Order o = saveBareOrder(client, "Code-X-001", OrderStatus.DRAFT);

        assertEquals(List.of(o), orderRepo.search(
                "code-x", null, null, null,
                null, null, null, null, null, null,
                client.getId(), null, null, null, null, null, null,
                SORT_BY_ID_ASC, 0, 10));

        assertEquals(List.of(o), orderRepo.search(
                "CODE-X", null, null, null,
                null, null, null, null, null, null,
                client.getId(), null, null, null, null, null, null,
                SORT_BY_ID_ASC, 0, 10));
    }

    // ---------- @ElementCollection round-trip ----------

    /**
     * Round-trips an Order with one Container that owns multiple {@link Destination} embeddables.
     *
     * Why this test matters for the migration: {@code container_destinations} is generated by
     * Hibernate from the {@code @ElementCollection} on {@link Container#getDestinations()} and is
     * one of the join tables the report query reads from
     * ({@code OrderRepositoryImpl.java:308}). If the dialect change subtly alters the table or
     * column names that Hibernate generates, this test fails before the deploy. It also covers
     * the {@code text} {@code columnDefinition} on {@link Destination#getProductDetails()}
     * (PG has a native {@code text} type so the round-trip should be lossless).
     */
    @Test
    void persistence_orderWithContainerHavingMultipleDestinations_roundTrips() {
        final Client client = clientForUser("u1");

        final Destination d1 = new Destination(DestinationType.TRM, "DEST-001", "100", "USD",
                "long product details that exceed varchar limits in some setups");
        final Destination d2 = new Destination(DestinationType.TLEA, "DEST-002", null, null, null);
        final Container container = new Container("CTR-X", ContainerType.ST_20, false,
                "BL-1", "PEMA-1", new ArrayList<>(List.of(d1, d2)));

        final Order order = new Order("RT-001", client,
                /*pema*/ true, /*port*/ true, /*transport*/ true,
                LocalDate.of(2025, 5, 1), LocalTime.of(10, 0),
                "Buenos Aires", "Rosario",
                /*freeLoad*/ false,
                /*driverData*/ null,
                /*customsData*/ null,
                new ArrayList<>(List.of(container)),
                new ArrayList<>(),
                /*consignee*/ null,
                /*createdByUserId*/ client.getUserId());

        final Order saved = orderRepo.save(order);
        final long savedId = saved.getId();

        // Force the row to flush and detach the in-memory entity so the next read goes to the DB.
        entityManager.flush();
        entityManager.clear();

        final Order reloaded = orderRepo.findById(savedId).orElseThrow();
        assertEquals("RT-001", reloaded.getCode());
        assertEquals(1, reloaded.getContainers().size());

        final Container reloadedContainer = reloaded.getContainers().get(0);
        assertEquals("CTR-X", reloadedContainer.getCode());
        assertEquals(ContainerType.ST_20, reloadedContainer.getType());
        assertEquals(2, reloadedContainer.getDestinations().size());

        final Set<String> codes = reloadedContainer.getDestinations().stream()
                .map(Destination::getCode)
                .collect(Collectors.toSet());
        assertEquals(Set.of("DEST-001", "DEST-002"), codes);

        // Spot-check the embeddable's text column round-trips intact.
        final Destination withDetails = reloadedContainer.getDestinations().stream()
                .filter(d -> "DEST-001".equals(d.getCode()))
                .findFirst()
                .orElseThrow();
        assertEquals("USD", withDetails.getCurrency());
        assertNotNull(withDetails.getProductDetails());
        assertTrue(withDetails.getProductDetails().contains("varchar limits"));
    }

    // ---------- helpers ----------

    private Client clientForUser(String userSuffix) {
        final User user = userRepo.save(
                new User("nick-" + userSuffix, "p", "mail-" + userSuffix + "@x.com", false));
        return clientRepo.save(new Client(user, "client-" + userSuffix, "phone", 1L));
    }

    private Order saveBareOrder(Client client, String code, OrderStatus status) {
        final Order order = new Order(code, client,
                false, false, false,
                LocalDate.now(), LocalTime.NOON,
                "origin", "target",
                false,
                null, null,
                new ArrayList<>(), new ArrayList<>(),
                null,
                client.getUserId());
        order.updateStatus(status);
        return orderRepo.save(order);
    }

    private Order saveOrderWithArrivalDate(Client client, String code, LocalDate arrivalDate) {
        final Order order = new Order(code, client,
                false, false, false,
                arrivalDate, LocalTime.NOON,
                "origin", "target",
                false,
                null, null,
                new ArrayList<>(), new ArrayList<>(),
                null,
                client.getUserId());
        return orderRepo.save(order);
    }
}
