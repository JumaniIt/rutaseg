package com.jumani.rutaseg.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DocumentTest {

    @Test
    public void testConstructor() {
        // Crear una instancia de Document usando el constructor
        Document document = new Document("Test Document", "test.pdf");

        // Verificar que los valores se establecieron correctamente en el constructor
        assertEquals("Test Document", document.getName());
        assertEquals("test.pdf", document.getResource());
        assertNotNull(document.getCreatedAt()); // Asegurarse de que la fecha de creación no sea nula
    }
}