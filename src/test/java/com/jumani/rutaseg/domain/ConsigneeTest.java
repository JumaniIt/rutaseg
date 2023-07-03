package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
public class ConsigneeTest {
    @Test
    public void testConsignee() {
        String name = "Juancho";
        long CUIT = 1234567890L;

        Consignee consignee = new Consignee(name, CUIT);

        assertNotNull(consignee);
        assertEquals(name, consignee.getName());
        assertEquals(CUIT, consignee.getCUIT());
    }

}
