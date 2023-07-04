package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class DriverDataTest {
    @Test
    public void testDriverData() {
        // Arrange
        String name = "John Doe";
        String phone = "1234567890";
        String company = "ABC Company";

        // Act
        DriverData driverData = new DriverData(name, phone, company);

        // Assert
        assertEquals(name, driverData.getName());
        assertEquals(phone, driverData.getPhone());
        assertEquals(company, driverData.getCompany());
    }
}