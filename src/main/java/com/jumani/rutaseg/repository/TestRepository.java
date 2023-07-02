package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    Optional<TestEntity> findOneByStringField(String stringField);
}
