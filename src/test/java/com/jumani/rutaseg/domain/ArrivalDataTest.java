package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ArrivalDataTest {

    @Test
    public void testCustomsData() {
        // Arrange
        String name = "John Doe";
        String phone = "1234567890";
        Long cuil = 123456789L;

        // Act
        CustomsData customsData = new CustomsData(name, phone, cuil);

        // Assert
        assertEquals(name, customsData.getName());
        assertEquals(phone, customsData.getPhone());
        assertEquals(cuil, customsData.getCUIL());
    }
}
