package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.jumani.rutaseg.TestDataGen.randomId;
import static com.jumani.rutaseg.TestDataGen.randomShortString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientTest {
    @Test
    public void initialization_AllAttributes() {
        String name = randomShortString();
        String phone = randomShortString();
        long CUIT = randomId();

        final User user = mock(User.class);

        when(user.getName()).thenReturn(name);

        Client client = new Client(user, phone, CUIT);

        assertEquals(user, client.getUser());
        assertEquals(phone, client.getPhone());
        assertEquals(CUIT, client.getCUIT());
        assertEquals(1, client.getConsignees().size());

        final Consignee selfConsignee = client.getConsignees().get(0);

        assertEquals(name, selfConsignee.getName());
        assertEquals(CUIT, selfConsignee.getCUIT());
    }

    @Test
    public void initialization_WithoutUser_Ok() {
        String phone = randomShortString();
        long CUIT = randomId();

        Client client = new Client(null, phone, CUIT);

        assertNull(client.getUser());
        assertEquals(phone, client.getPhone());
        assertEquals(CUIT, client.getCUIT());
        assertTrue(client.getConsignees().isEmpty());
    }

    @Test
    public void initialization_WithoutCUIT_Ok() {
        String name = randomShortString();
        String phone = randomShortString();

        final User user = mock(User.class);

        when(user.getName()).thenReturn(name);

        Client client = new Client(user, phone, null);

        assertEquals(user, client.getUser());
        assertEquals(phone, client.getPhone());
        assertNull(client.getCUIT());
        assertTrue(client.getConsignees().isEmpty());
    }

    @Test
    void addConsignee_Ok() {
        String name = "Juan";
        String phone = randomShortString();
        long CUIT = 1L;

        final User user = mock(User.class);

        when(user.getName()).thenReturn(name);

        Client client = new Client(user, phone, CUIT);

        final String consigneeName = "Pepe";
        final long consigneeCUIT = 2L;

        final Optional<Error> error = client.addConsignee(new Consignee(consigneeName, consigneeCUIT));

        assertTrue(error.isEmpty());
        assertEquals(2, client.getConsignees().size());

        final Consignee secondConsignee = client.getConsignees().get(1);

        assertEquals(consigneeName, secondConsignee.getName());
        assertEquals(consigneeCUIT, secondConsignee.getCUIT());
    }

    @Test
    void addConsignee_DuplicatedName_ReturnError() {
        String name = "Juan";
        String phone = randomShortString();
        long CUIT = 1L;

        final User user = mock(User.class);

        when(user.getName()).thenReturn(name);

        Client client = new Client(user, phone, CUIT);

        final String consigneeName = "Pepe";
        final long consigneeCUIT = 2L;

        final Error expectedError = new Error("duplicated_consignee", "consignee with the same name or CUIT already exists");

        client.addConsignee(new Consignee(consigneeName, consigneeCUIT));
        final Optional<Error> error = client.addConsignee(new Consignee(consigneeName, 3L));

        assertEquals(Optional.of(expectedError), error);
    }

    @Test
    void addConsignee_DuplicatedCUIT_ReturnError() {
        String name = "Juan";
        String phone = randomShortString();
        long CUIT = 1L;

        final User user = mock(User.class);

        when(user.getName()).thenReturn(name);

        Client client = new Client(user, phone, CUIT);

        final String consigneeName = "Pepe";
        final long consigneeCUIT = 2L;

        final Error expectedError = new Error("duplicated_consignee", "consignee with the same name or CUIT already exists");

        client.addConsignee(new Consignee(consigneeName, consigneeCUIT));
        final Optional<Error> error = client.addConsignee(new Consignee("Mario", consigneeCUIT));

        assertEquals(Optional.of(expectedError), error);
    }
}
