package com.jumani.rutaseg.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomsDataTest {
    @Test
    public void testCustomsData() {
        // Arrange
        String name = "John Doe";
        String phone = "1234567890";
        Long CUIT = 123456789L;

        // Act
        CustomsData customsData = new CustomsData(name, phone, CUIT);

        // Assert
        assertEquals(name, customsData.getName());
        assertEquals(phone, customsData.getPhone());
        assertEquals(CUIT, customsData.getCUIT());
    }
}

