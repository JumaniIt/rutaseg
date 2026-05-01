package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.IntegrationTest;
import com.jumani.rutaseg.domain.Client;
import com.jumani.rutaseg.domain.User;
import com.jumani.rutaseg.repository.client.ClientRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class UserRepositoryImplTest extends IntegrationTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    ClientRepository clientRepo;

    @Test
    void search_byNicknameSubstring_returnsMatch() {
        final User u1 = userRepo.save(new User("alpha_one", "p", "alpha_one@x.com", false));
        final User u2 = userRepo.save(new User("beta_two", "p", "beta_two@x.com", false));

        // exclusive substring → only u1
        assertEquals(1L, userRepo.count(null, "alpha", null, null));
        assertEquals(List.of(u1), userRepo.search(null, "alpha", null, null, 0, 10));

        // shared substring → both
        assertEquals(2L, userRepo.count(null, "_", null, null));
        assertEquals(List.of(u1, u2), userRepo.search(null, "_", null, null, 0, 10));

        // miss
        assertEquals(0L, userRepo.count(null, "zzz", null, null));
        assertTrue(userRepo.search(null, "zzz", null, null, 0, 10).isEmpty());
    }

    @Test
    void search_byEmailSubstring_returnsMatch() {
        final User u1 = userRepo.save(new User("u1", "p", "alice@example.com", false));
        final User u2 = userRepo.save(new User("u2", "p", "bob@example.com", false));

        assertEquals(1L, userRepo.count(null, null, "alice", null));
        assertEquals(List.of(u1), userRepo.search(null, null, "alice", null, 0, 10));

        assertEquals(2L, userRepo.count(null, null, "@example", null));
        assertEquals(List.of(u1, u2), userRepo.search(null, null, "@example", null, 0, 10));
    }

    @Test
    void search_byAdminFlag_filtersBothBranches() {
        final User admin = userRepo.save(new User("a", "p", "a@x.com", true));
        final User regular = userRepo.save(new User("n", "p", "n@x.com", false));

        assertEquals(1L, userRepo.count(true, null, null, null));
        assertEquals(List.of(admin), userRepo.search(true, null, null, null, 0, 10));

        assertEquals(1L, userRepo.count(false, null, null, null));
        assertEquals(List.of(regular), userRepo.search(false, null, null, null, 0, 10));

        // null = no filter
        assertEquals(2L, userRepo.count(null, null, null, null));
    }

    @Test
    void search_withClientSubquery_returnsCorrectBranches() {
        final User userWithClient = userRepo.save(new User("w", "p", "w@x.com", false));
        final User userWithoutClient = userRepo.save(new User("wo", "p", "wo@x.com", false));
        clientRepo.save(new Client(userWithClient, "client-name", "phone", 1L));

        // withClient = true → exists subquery
        assertEquals(1L, userRepo.count(null, null, null, true));
        assertEquals(List.of(userWithClient), userRepo.search(null, null, null, true, 0, 10));

        // withClient = false → not exists subquery
        assertEquals(1L, userRepo.count(null, null, null, false));
        assertEquals(List.of(userWithoutClient), userRepo.search(null, null, null, false, 0, 10));
    }

    @Test
    void search_paginationOffsetLimit() {
        for (int i = 0; i < 5; i++) {
            userRepo.save(new User("u" + i, "p", "u" + i + "@x.com", false));
        }

        assertEquals(5L, userRepo.count(null, null, null, null));
        assertEquals(2, userRepo.search(null, null, null, null, 0, 2).size());
        assertEquals(2, userRepo.search(null, null, null, null, 2, 2).size());
        assertEquals(1, userRepo.search(null, null, null, null, 4, 2).size());
    }

    /**
     * Migration regression test (MySQL → PostgreSQL). Asserts that searching by nickname is
     * case-insensitive — MySQL's default {@code utf8mb4_general_ci} collation provides this for free,
     * and H2 in {@code MODE=MySQL} replicates it. PostgreSQL's {@code LIKE} is case-sensitive, so
     * the migration must wrap {@code nicknameLike} in {@code cb.lower(...)}
     * ({@code UserRepositoryImpl.java:75}) to keep this test green after the cutover.
     */
    @Test
    void search_byNicknameMixedCase_isCaseInsensitive() {
        final User u = userRepo.save(new User("AlphaOne", "p", "AlphaOne@x.com", false));

        // lowercase input must match mixed-case data
        assertEquals(1L, userRepo.count(null, "alphaone", null, null));
        assertEquals(List.of(u), userRepo.search(null, "alphaone", null, null, 0, 10));

        // uppercase input must match too
        assertEquals(1L, userRepo.count(null, "ALPHAONE", null, null));
        assertEquals(List.of(u), userRepo.search(null, "ALPHAONE", null, null, 0, 10));
    }

    /**
     * Migration regression test — same rationale as {@link #search_byNicknameMixedCase_isCaseInsensitive()},
     * for the {@code emailLike} predicate in {@code UserRepositoryImpl.java:79}.
     */
    @Test
    void search_byEmailMixedCase_isCaseInsensitive() {
        final User u = userRepo.save(new User("u", "p", "Alice@Example.com", false));

        assertEquals(1L, userRepo.count(null, null, "alice@example.com", null));
        assertEquals(List.of(u), userRepo.search(null, null, "alice@example.com", null, 0, 10));

        assertEquals(1L, userRepo.count(null, null, "ALICE@EXAMPLE.COM", null));
        assertEquals(List.of(u), userRepo.search(null, null, "ALICE@EXAMPLE.COM", null, 0, 10));
    }
}
