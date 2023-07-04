package com.jumani.rutaseg.domain;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    @Test
    public void testOrderInitialization() {
        // Arrange
        boolean pema = true;
        boolean port = false;
        boolean transport = true;
        OrderStatus status = OrderStatus.DRAFT;
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime finishedAt = ZonedDateTime.now().plusHours(2);
        ArrivalData arrivalData = new ArrivalData(LocalDate.now(), LocalTime.now(), "Morning", true, Destination.TRM, "FOB", Currency.USD);
        DriverData driverData = new DriverData("John Doe", "1234567890", "ABC Company");
        CustomsData customsData = new CustomsData("Customs Name", "9876543210", 123456789L);

        // Act
        Order order = new Order(pema, port, transport, status, createdAt,finishedAt, arrivalData, driverData, customsData);

        // Assert
        assertFalse(order.isPema());
        assertFalse(order.isPort());
        assertFalse(order.isTransport());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(finishedAt, order.getFinishedAt());
        assertEquals(arrivalData, order.getArrivalData());
        assertEquals(driverData, order.getDriverData());
        assertEquals(customsData, order.getCustomsData());
    }
}
