package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@FieldNameConstants
public class UserRepositoryImpl implements UserRepositoryExtended {

    private final EntityManager entityManager;

    public List<User> search(@Nullable Boolean admin,
                             @Nullable String nicknameLike,
                             @Nullable String emailLike,
                             int offset,
                             int limit) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root);
        criteriaQuery.where(createPredicates(builder, root, admin, nicknameLike, emailLike));

        return entityManager.createQuery(criteriaQuery)
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultList();
    }

    @Override
    public long count(@Nullable Boolean admin,
                      @Nullable String nicknameLike,
                      @Nullable String emailLike) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(createPredicates(builder, root, admin, nicknameLike, emailLike));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private Predicate[] createPredicates(CriteriaBuilder builder, Root<User> root,
                                         @Nullable Boolean admin,
                                         @Nullable String nicknameLike,
                                         @Nullable String emailLike) {

        final List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(admin)) {
            predicates.add(builder.equal(root.get(User.Fields.admin), admin));
        }

        if (Objects.nonNull(nicknameLike)) {
            predicates.add(builder.like(root.get(User.Fields.nickname), "%" + nicknameLike + "%"));
        }

        if (Objects.nonNull(emailLike)) {
            predicates.add(builder.like(root.get(User.Fields.email), "%" + emailLike + "%"));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
