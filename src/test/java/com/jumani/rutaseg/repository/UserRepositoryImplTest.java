package com.jumani.rutaseg.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.jumani.rutaseg.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImplTest {

    private UserRepositoryImpl userRepoImpl;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<User> criteriaQuery;
    private Root<User> root;

    @BeforeEach
    public void setup() {
        entityManager = mock(EntityManager.class);
        userRepoImpl = new UserRepositoryImpl(entityManager);
        criteriaBuilder = mock(CriteriaBuilder.class);
        criteriaQuery = mock(CriteriaQuery.class);
        root = mock(Root.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(User.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
    }

    @Test
    public void testSearch() {
        // Preparar datos de prueba
        List<User> userList = new ArrayList<>();
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        userList.add(user1);

        // Simular el comportamiento de TypedQuery
        TypedQuery<User> query = mock(TypedQuery.class);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(userList);

        // Llamar al método de búsqueda
        List<User> result = userRepoImpl.search(true, null, null, 0, 10);

        // Verificar el resultado
        assertEquals(userList, result);
    }

    @Test
    public void testCount() {
        // Simular el comportamiento de EntityManager y TypedQuery
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Long> criteriaQuery = mock(CriteriaQuery.class);
        Root<User> root = mock(Root.class);
        TypedQuery<Long> query = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(User.class)).thenReturn(root);
        when(criteriaBuilder.equal(root.get(User.Fields.admin), true)).thenReturn(mock(Predicate.class));
        when(entityManager.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(42L);

        // Llamar al método de conteo
        long result = userRepoImpl.count(true, null, null);

        // Verificar el resultado
        assertEquals(42L, result);
    }
}
