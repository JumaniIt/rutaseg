package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.TestDataGen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestEntityTest {

    @Test
    void inicializacion_Ok() {
        final String stringField = TestDataGen.randomShortString();
        final Long longField = TestDataGen.randomId();
        final TestEnum enumField = TestDataGen.randomEnum(TestEnum.class);

        final TestEntity testEntity = new TestEntity(stringField, longField, enumField);

        assertAll("Resultado esperado",
                () -> assertEquals(stringField, testEntity.getStringField()),
                () -> assertEquals(longField, testEntity.getLongField()),
                () -> assertEquals(enumField, testEntity.getEnumField())
        );
    }
}