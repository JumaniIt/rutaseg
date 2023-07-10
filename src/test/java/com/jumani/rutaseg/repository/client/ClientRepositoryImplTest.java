package com.jumani.rutaseg.repository.client;

import com.jumani.rutaseg.IntegrationTest;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class ClientRepositoryImplTest extends IntegrationTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    ClientRepository clientRepo;

    @Test
    void search_Variants_Ok() {
        final String nameOne = "name1";
        final String phoneOne = "phone1";
        final long cuitOne = 1L;

        final String nameTwo = "name2";
        final String phoneTwo = "phone2";
        final long cuitTwo = 2L;

        final User userOne = userRepo.save(new User("nickname1", "password1", "email1", false));
        final User userTwo = userRepo.save(new User("nickname1", "password1", "email2", false));

        final Client clientOne = clientRepo.save(new Client(userOne, nameOne, phoneOne, cuitOne));
        final Client clientTwo = clientRepo.save(new Client(userTwo, nameTwo, phoneTwo, cuitTwo));

        final List<Client> justClientOne = List.of(clientOne);
        final List<Client> justClientTwo = List.of(clientTwo);
        final List<Client> bothClients = List.of(clientOne, clientTwo);

        // by user id
        assertEquals(justClientOne, clientRepo.search(clientOne.getUserId(), null, null, null, 2));
        assertEquals(justClientTwo, clientRepo.search(clientTwo.getUserId(), null, null, null, 1));

        // by name like
        assertEquals(bothClients, clientRepo.search(null, "am", null, null, 2));
        assertEquals(justClientOne, clientRepo.search(null, "ame1", null, null, 2));

        // by phone like
        assertEquals(bothClients, clientRepo.search(null, "name", "phone", null, 10));
        assertEquals(justClientTwo, clientRepo.search(null, "name", "2", null, 10));

        // by cuit
        assertEquals(justClientOne, clientRepo.search(null, null, null, cuitOne, 10));
        assertEquals(justClientTwo, clientRepo.search(null, null, null, cuitTwo, 10));

        // by everything
        assertEquals(justClientOne, clientRepo.search(clientOne.getUserId(), "name", "phone", cuitOne, 10));
        assertEquals(justClientTwo, clientRepo.search(clientTwo.getUserId(), "name", "phone", cuitTwo, 10));

        // by nothing
        assertEquals(bothClients, clientRepo.search(null, null, null, null, 2));

        // by nothing limit 1
        assertEquals(justClientOne, clientRepo.search(null, null, null, null, 1));

        // no results
        assertTrue(clientRepo.search(clientOne.getUserId(), "x", "phone", cuitOne, 1).isEmpty());
    }
}