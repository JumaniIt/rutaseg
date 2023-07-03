package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
public class ClientTest {
    @Test
    public void testClient() {
        String phone = "123456789";
        long CUIT = 1234567890L;
        Consignee consignee = new Consignee("Juancho", CUIT);
        Client client = new Client(phone, consignee);

        assertNotNull(client);
        assertEquals(phone, client.getPhone());
        assertEquals(consignee, client.getConsignee());
    }



}
