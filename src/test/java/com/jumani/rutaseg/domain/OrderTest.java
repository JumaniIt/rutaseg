package com.jumani.rutaseg.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jumani.rutaseg.TestDataGen.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderInitialization() {
        // Arrange
        String code = randomShortString();
        boolean pema = randomBoolean();
        boolean port = randomBoolean();
        boolean transport = randomBoolean();
        final Origin origin = randomEnum(Origin.class);
        long createdByUserId = randomId();
        final DestinationType destinationType = randomEnum(DestinationType.class);
        final String destinationName = randomShortString();

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), origin,
                "Morning", true, destinationType, destinationName, "FOB", "USD");
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210");
        Client client = new Client(new User("John", "password", "john@example.com", false),
                "name", "1234567890", 123456789L);
        Container container1 = new Container("ABC123",Measures.STANDARD_DRY_20_20_ST , false, "PEMA1");
        Container container2 = new Container("XYZ789", Measures.STANDARD_OPEN_SIDE_20_20_OS, true, "PEMA2");
        List<Container> containers = Arrays.asList(container1, container2);
        ConsigneeData consigneeData = new ConsigneeData("Consignee Name", 123456789L);

        // Act
        Order order = new Order(code, client, pema, port, transport, arrivalData, driverData, customsData, createdByUserId, containers, consigneeData);

        // Assert
        assertEquals(code, order.getCode());
        assertEquals(pema, order.isPema());
        assertEquals(port, order.isPort());
        assertEquals(transport, order.isTransport());
        assertSame(arrivalData, order.getArrivalData());
        assertSame(driverData, order.getDriverData());
        assertSame(customsData, order.getCustomsData());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getFinishedAt());
        assertEquals(createdByUserId, order.getCreatedByUserId());
        assertSame(client, order.getClient());
        assertSame(consigneeData, order.getConsignee());
        assertSame(containers, order.getContainers());
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
                "Morning",
                true,
                DestinationType.TLEA,
                "Warehouse A",
                "FOB",
                "USD"
        );
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
                originalContainers, originalConsigneeData);

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
                "Afternoon",
                false,
                DestinationType.TLAT,
                "Customer B",
                "CIF",
                "EUR"
        );
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
        updatedContainers.add(new Container("ABC123", Measures.STANDARD_DRY_20_20_ST, false, "PEMA1"));
        updatedContainers.add(new Container("XYZ789", Measures.STANDARD_OPEN_SIDE_20_20_OS, true, "PEMA2"));
        ConsigneeData updatedConsigneeData = new ConsigneeData(
                "New Consignee",
                54321L
        );

        // Act
        order.update(updatedCode, updatedClient, updatedPema, updatedPort, updatedTransport,
                updatedArrivalData, updatedDriverData, updatedCustomsData,
                updatedContainers, updatedConsigneeData);

        // Assert
        assertEquals(updatedCode, order.getCode());
        assertEquals(updatedClient, order.getClient());
        assertEquals(updatedPema, order.isPema());
        assertEquals(updatedPort, order.isPort());
        assertEquals(updatedTransport, order.isTransport());
        assertSame(updatedArrivalData, order.getArrivalData());
        assertSame(updatedDriverData, order.getDriverData());
        assertSame(updatedCustomsData, order.getCustomsData());
        assertSame(updatedContainers, order.getContainers());
        assertSame(updatedConsigneeData, order.getConsignee());
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
        final String destinationName = randomShortString();
        final Origin origin = randomEnum(Origin.class);

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), origin,
                "Morning", true, destinationType, destinationName, "FOB", "USD");
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210");
        List<Container> containers = new ArrayList<>();
        ConsigneeData consigneeData = new ConsigneeData("Consignee Name", 123456789L);

        Container container1 = new Container("ABC123", Measures.STANDARD_DRY_20_20_ST, false, "PEMA1");
        Container container2 = new Container("XYZ789", Measures.STANDARD_OPEN_SIDE_20_20_OS, true, "PEMA2");
        containers.addAll(Arrays.asList(container1, container2));

        Order order = new Order(randomShortString(), client, pema, port, transport, arrivalData, driverData, customsData,
                createdByUserId, containers, consigneeData);

        OrderStatus newStatus = OrderStatus.DRAFT;

        order.updateStatus(newStatus);

        assertEquals(newStatus, order.getStatus());
    }


}
