package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.TestDataGen;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderInitialization() {
        // Arrange
        boolean pema = TestDataGen.randomBoolean();
        boolean port = TestDataGen.randomBoolean();
        boolean transport = TestDataGen.randomBoolean();
        long createdByUserId = TestDataGen.randomId();
        final Destination destination = TestDataGen.randomEnum(Destination.class);

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), "Morning", true, destination, "FOB", Currency.USD);
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210", 123456789L);
        Client client = new Client(new User("John", "password", "john@example.com", false), "name", "1234567890", 123456789L);

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