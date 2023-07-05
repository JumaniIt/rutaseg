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
        final Destination destination = TestDataGen.randomEnum(Destination.class);

        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), "Morning", true, destination, "FOB", Currency.USD);
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210", 123456789L);

        // Act
        Order order = new Order(pema, port, transport, arrivalData, driverData, customsData);

        // Assert
        assertEquals(pema, order.isPema());
        assertEquals(port, order.isPort());
        assertEquals(transport, order.isTransport());
        assertEquals(arrivalData, order.getArrivalData());
        assertEquals(driverData, order.getDriverData());
        assertEquals(customsData, order.getCustomsData());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getFinishedAt());
    }
}