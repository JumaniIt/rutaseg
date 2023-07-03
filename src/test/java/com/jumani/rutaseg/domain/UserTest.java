package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUser() {
        String name = "John";
        String password = "password123";
        String email = "john@example.com";
        boolean admin = true;
        String phone = "123456789";
        long CUIT = 1234567890L;
        Consignee consignee = new Consignee("Juancho", CUIT);
        Client client = new Client(phone, consignee);

        User user = new User(name, password, email, admin, client);

        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(admin, user.isAdmin());
        assertEquals(client, user.getClient());
    }
}