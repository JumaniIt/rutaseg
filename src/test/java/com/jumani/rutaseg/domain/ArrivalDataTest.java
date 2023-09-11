package com.jumani.rutaseg.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ArrivalDataTest {
    @Test
    public void testArrivalData() {
        // Arrange
        ArrivalData arrivalData = new ArrivalData(
                LocalDate.of(2023, 9, 1),
                LocalTime.of(10, 30),
                Origin.EXOLGAN,
                "Morning",
                false,
                DestinationType.TLEA,
                "Warehouse A",
                "FOB123",
                "USD"
        );

        // Verificar que los valores se establecieron correctamente
        assertEquals(LocalDate.of(2023, 9, 1), arrivalData.getArrivalDate());
        assertEquals(LocalTime.of(10, 30), arrivalData.getArrivalTime());
        assertEquals(Origin.EXOLGAN, arrivalData.getOrigin());
        assertEquals("Morning", arrivalData.getTurn());
        assertFalse(arrivalData.isFreeLoad());
        assertEquals(DestinationType.TLEA, arrivalData.getDestinationType());
        assertEquals("Warehouse A", arrivalData.getDestinationName());
        assertEquals("FOB123", arrivalData.getFob());
        assertEquals("USD", arrivalData.getCurrency());
    }
}
