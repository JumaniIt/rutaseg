package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Optional;

@Getter
@Entity
@Table(name = "test_entities")
public class TestEntity {

    @Id
    // generación autoincremental de la db, lo asigna automáticamente el framework (Hibernate)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "string_field")
    private String stringField;

    @Column(name = "long_field")
    private Long longField;

    @Column(name = "enum_field")
    // deserializa usando el nombre del enum (ej.: "A", "B")
    @Enumerated(EnumType.STRING)
    private TestEnum enumField;

    // Esto lo necesita Hibernate para convertir el registro de la DB en una instancia
    private TestEntity() {
    }

    public TestEntity(String stringField, Long longField, TestEnum enumField) {
        this.stringField = stringField;
        this.longField = longField;
        this.enumField = enumField;
    }

    public final Optional<Error> testMethod(long someLong) {
        Error error = null;

        if (someLong <= 0) {
            error = new Error("invalid_some_long", "some long must be positive");
        } else {
            // do something here...
        }

        return Optional.ofNullable(error);
    }

}
