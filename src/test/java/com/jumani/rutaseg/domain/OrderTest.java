package com.jumani.rutaseg.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jumani.rutaseg.TestDataGen.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderInitialization_WithContainers() {
        // Arrange
        String code = randomShortString();
        boolean pema = randomBoolean();
        boolean port = randomBoolean();
        boolean transport = randomBoolean();
        final Origin origin = randomEnum(Origin.class);
        long createdByUserId = randomId();
        final DestinationType destinationType = randomEnum(DestinationType.class);
        final String destinationCode = randomShortString();

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), origin,
                false, destinationType, destinationCode, "FOB", "USD", null);
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210");
        Client client = new Client(new User("John", "password", "john@example.com", false),
                "name", "1234567890", 123456789L);
        Container container1 = new Container("ABC123", Measures.ST_20, false, "BL1", "PEMA1");
        Container container2 = new Container("XYZ789", Measures.OS_20, true, "BL2", "PEMA2");
        List<Container> containers = Arrays.asList(container1, container2);
        ConsigneeData consigneeData = new ConsigneeData("Consignee Name", 123456789L);

        // Act
        Order order = new Order(code, client, pema, port, transport, arrivalData, driverData, customsData, createdByUserId, containers, null, consigneeData);

        // Assert
        assertEquals(code, order.getCode());
        assertEquals(pema, order.isPema());
        assertEquals(port, order.isPort());
        assertEquals(transport, order.isTransport());
        assertEquals(arrivalData, order.getArrivalData());
        assertEquals(driverData, order.getDriverData());
        assertEquals(customsData, order.getCustomsData());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getFinishedAt());
        assertEquals(createdByUserId, order.getCreatedByUserId());
        assertEquals(client, order.getClient());
        assertEquals(consigneeData, order.getConsignee());
        assertEquals(containers, order.getContainers());
    }

    @Test
    public void testOrderInitialization_WithFreeLoad() {
        // Arrange
        String code = randomShortString();
        boolean pema = randomBoolean();
        boolean port = randomBoolean();
        boolean transport = randomBoolean();
        final Origin origin = randomEnum(Origin.class);
        long createdByUserId = randomId();
        final DestinationType destinationType = randomEnum(DestinationType.class);
        final String destinationCode = randomShortString();

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), origin,
                true, destinationType, destinationCode, "FOB", "USD", null);
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210");
        Client client = new Client(new User("John", "password", "john@example.com", false),
                "name", "1234567890", 123456789L);
        FreeLoad freeLoad1 = new FreeLoad("123456", FreeLoadType.SEMI, "20kg", "Guide1", "PEMA1");
        FreeLoad freeLoad2 = new FreeLoad("123456", FreeLoadType.SEMI, "20kg", "Guide1", "PEMA1");
        List<FreeLoad> freeLoads = Arrays.asList(freeLoad1, freeLoad2);
        ConsigneeData consigneeData = new ConsigneeData("Consignee Name", 123456789L);

        // Act
        Order order = new Order(code, client, pema, port, transport, arrivalData, driverData, customsData, createdByUserId, Collections.emptyList(), freeLoads, consigneeData);

        // Assert
        assertEquals(code, order.getCode());
        assertEquals(pema, order.isPema());
        assertEquals(port, order.isPort());
        assertEquals(transport, order.isTransport());
        assertEquals(arrivalData, order.getArrivalData());
        assertEquals(driverData, order.getDriverData());
        assertEquals(customsData, order.getCustomsData());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getFinishedAt());
        assertEquals(createdByUserId, order.getCreatedByUserId());
        assertEquals(client, order.getClient());
        assertEquals(consigneeData, order.getConsignee());
        assertEquals(freeLoads, order.getFreeLoads());
    }

    @Test
    void testUpdateMethod() {
        // Arrange
        Client originalClient = new Client(new User("John", "password", "john@example.com", false),
                "Original Client", "1234567890", 123456789L);
        boolean originalPema = false;
        boolean originalPort = true;
        boolean originalTransport = true;
        ArrivalData originalArrivalData = new ArrivalData(
                LocalDate.of(2023, 6, 1),
                LocalTime.of(9, 0),
                Origin.EZEIZA,
                true,
                DestinationType.TLEA,
                "Warehouse A",
                "FOB",
                "USD",
                null);
        DriverData originalDriverData = new DriverData(
                "John Doe",
                "1234567890",
                "ABC Company"
        );
        CustomsData originalCustomsData = new CustomsData(
                "Customs Name",
                "9876543210"
        );
        long originalCreatedByUserId = 12345L;
        List<Container> originalContainers = new ArrayList<>();
        ConsigneeData originalConsigneeData = new ConsigneeData(
                "Consignee Name",
                67890L
        );

        Order order = new Order("code-1", originalClient, originalPema, originalPort, originalTransport,
                originalArrivalData, originalDriverData, originalCustomsData, originalCreatedByUserId,
                originalContainers, null, originalConsigneeData);

        String updatedCode = "code-2";
        Client updatedClient = new Client(new User("Jane", "password", "jane@example.com", false),
                "Updated Client", "9876543210", 987654321L);
        boolean updatedPema = true;
        boolean updatedPort = false;
        boolean updatedTransport = false;
        ArrivalData updatedArrivalData = new ArrivalData(
                LocalDate.of(2023, 7, 15),
                LocalTime.of(14, 30),
                Origin.EXOLGAN,
                false,
                DestinationType.TLAT,
                "Customer B",
                "CIF",
                "EUR",
                null);
        DriverData updatedDriverData = new DriverData(
                "Jane Smith",
                "9876543210",
                "XYZ Transport"
        );
        CustomsData updatedCustomsData = new CustomsData(
                "Updated Customs",
                "1234567890"
        );
        List<Container> updatedContainers = new ArrayList<>();
        updatedContainers.add(new Container("ABC123", Measures.ST_20, false, "BL1", "PEMA1"));
        updatedContainers.add(new Container("XYZ789", Measures.OS_20, true, "BL2", "PEMA2"));

        FreeLoad freeLoad1 = new FreeLoad("123456", FreeLoadType.SEMI, "20kg", "Guide1", "PEMA1");
        FreeLoad freeLoad2 = new FreeLoad("123456", FreeLoadType.SEMI, "20kg", "Guide1", "PEMA1");
        List<FreeLoad> updatedFreeLoads = Arrays.asList(freeLoad1, freeLoad2);

        ConsigneeData updatedConsigneeData = new ConsigneeData(
                "New Consignee",
                54321L
        );

        // Act
        order.update(updatedCode, updatedClient, updatedPema, updatedPort, updatedTransport,
                updatedArrivalData, updatedDriverData, updatedCustomsData,
                updatedContainers, updatedFreeLoads, updatedConsigneeData);

        // Assert
        assertEquals(updatedCode, order.getCode());
        assertEquals(updatedClient, order.getClient());
        assertEquals(updatedPema, order.isPema());
        assertEquals(updatedPort, order.isPort());
        assertEquals(updatedTransport, order.isTransport());
        assertEquals(updatedArrivalData, order.getArrivalData());
        assertEquals(updatedDriverData, order.getDriverData());
        assertEquals(updatedCustomsData, order.getCustomsData());
        assertEquals(updatedContainers, order.getContainers());
        assertEquals(updatedFreeLoads, order.getFreeLoads());
        assertEquals(updatedConsigneeData, order.getConsignee());
    }

    @Test
    void testUpdateStatusMethod() {
        Client client = new Client(new User("John", "password", "john@example.com", false),
                "name", "1234567890", 123456789L);

        boolean pema = randomBoolean();
        boolean port = randomBoolean();
        boolean transport = randomBoolean();
        long createdByUserId = randomId();
        final DestinationType destinationType = randomEnum(DestinationType.class);
        final String destinationCode = randomShortString();
        final Origin origin = randomEnum(Origin.class);

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), origin,
                true, destinationType, destinationCode, "FOB", "USD", null);
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210");
        ConsigneeData consigneeData = new ConsigneeData("Consignee Name", 123456789L);

        Container container1 = new Container("ABC123", Measures.ST_20, false, "BL1", "PEMA1");
        Container container2 = new Container("XYZ789", Measures.OS_20, true, "BL2", "PEMA2");
        List<Container> containers = new ArrayList<>(Arrays.asList(container1, container2));

        Order order = new Order(randomShortString(), client, pema, port, transport, arrivalData, driverData, customsData,
                createdByUserId, containers, null, consigneeData);

        OrderStatus newStatus = OrderStatus.DRAFT;

        order.updateStatus(newStatus);

        assertEquals(newStatus, order.getStatus());
    }


}
