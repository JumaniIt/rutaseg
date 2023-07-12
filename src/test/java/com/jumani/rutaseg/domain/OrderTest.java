package com.jumani.rutaseg.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.jumani.rutaseg.TestDataGen.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderInitialization() {
        // Arrange
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

        // Act
        Order order = new Order(client, pema, port, transport, arrivalData, driverData, customsData, createdByUserId);

        // Assert
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

    }
}