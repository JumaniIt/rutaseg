package com.jumani.rutaseg.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CostTest {

    @Test
    public void testConstructor() {
        // Crear una instancia de Cost usando el constructor
        Cost cost = new Cost(100.0, "Description", CostType.EXTRA, 1);

        // Verificar que los valores se establecieron correctamente en el constructor
        assertEquals(100.0, cost.getAmount());
        assertEquals("Description", cost.getDescription());
        assertEquals(CostType.EXTRA, cost.getType());
        assertEquals(1, cost.getCreatedByUserId());
        assertNull(cost.getUpdatedAt()); // Debe ser nulo inicialmente
    }

    @Test
    public void testUpdate() {
        // Crear una instancia de Cost
        Cost cost = new Cost();

        // Llamar al método update para actualizar los valores
        cost.update(200.0, "Updated Description", CostType.PORT);

        // Verificar que los valores se actualizaron correctamente
        assertEquals(200.0, cost.getAmount());
        assertEquals("Updated Description", cost.getDescription());
        assertEquals(CostType.PORT, cost.getType());
        // La fecha de actualización debe haberse establecido
        // Verificamos que no sea nula
        assertNotNull(cost.getUpdatedAt());
    }
}