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
    void searchAndCount_Variants_Ok() {
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
        final Client clientThree = clientRepo.save(new Client(null, "Mark", "3", 3L));

        final List<Client> justClientOne = List.of(clientOne);
        final List<Client> justClientTwo = List.of(clientTwo);
        final List<Client> justClientThree = List.of(clientThree);
        final List<Client> clientOneAndTwo = List.of(clientOne, clientTwo);
        final List<Client> clientTwoAndThree = List.of(clientTwo, clientThree);
        final List<Client> allClients = List.of(clientOne, clientTwo, clientThree);

        // by user id
        assertEquals(1, clientRepo.count(clientOne.getUserId(), null, null, null));
        assertEquals(justClientOne, clientRepo.search(clientOne.getUserId(), null, null, null, 0, 2));
        assertEquals(1, clientRepo.count(clientTwo.getUserId(), null, null, null));
        assertEquals(justClientTwo, clientRepo.search(clientTwo.getUserId(), null, null, null, 0, 1));

        // by not having user
        assertEquals(1, clientRepo.count(-1L, null, null, null));
        assertEquals(justClientThree, clientRepo.search(-1L, null, null, null, 0, 1));

        // by name like
        assertEquals(2, clientRepo.count(null, "am", null, null));
        assertEquals(clientOneAndTwo, clientRepo.search(null, "am", null, null, 0, 2));
        assertEquals(1, clientRepo.count(null, "ame1", null, null));
        assertEquals(justClientOne, clientRepo.search(null, "ame1", null, null, 0, 2));

        // by phone like
        assertEquals(2, clientRepo.count(null, "name", "phone", null));
        assertEquals(clientOneAndTwo, clientRepo.search(null, "name", "phone", null, 0, 10));
        assertEquals(1, clientRepo.count(null, "name", "2", null));
        assertEquals(justClientTwo, clientRepo.search(null, "name", "2", null, 0, 10));

        // by cuit
        assertEquals(1, clientRepo.count(null, null, null, cuitOne));
        assertEquals(justClientOne, clientRepo.search(null, null, null, cuitOne, 0, 10));
        assertEquals(1, clientRepo.count(null, null, null, cuitTwo));
        assertEquals(justClientTwo, clientRepo.search(null, null, null, cuitTwo, 0, 10));

        // by everything
        assertEquals(1, clientRepo.count(clientOne.getUserId(), "name", "phone", cuitOne));
        assertEquals(justClientOne, clientRepo.search(clientOne.getUserId(), "name", "phone", cuitOne, 0, 10));
        assertEquals(1, clientRepo.count(clientTwo.getUserId(), "name", "phone", cuitTwo));
        assertEquals(justClientTwo, clientRepo.search(clientTwo.getUserId(), "name", "phone", cuitTwo, 0, 10));

        // by nothing
        assertEquals(3, clientRepo.count(null, null, null, null));
        assertEquals(allClients, clientRepo.search(null, null, null, null, 0, 4));

        // by nothing limit 1
        assertEquals(3, clientRepo.count(null, null, null, null));
        assertEquals(justClientOne, clientRepo.search(null, null, null, null, 0, 1));

        // by nothing limit 1 offset 2
        assertEquals(clientTwoAndThree, clientRepo.search(null, null, null, null, 1, 2));

        // no results
        assertTrue(clientRepo.search(clientOne.getUserId(), "x", "phone", cuitOne, 0, 1).isEmpty());
    }
}