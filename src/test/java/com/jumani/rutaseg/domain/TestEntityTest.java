package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testMethod_ReturnError() {
        final String stringField = TestDataGen.randomShortString();
        final Long longField = TestDataGen.randomId();
        final TestEnum enumField = TestDataGen.randomEnum(TestEnum.class);

        final TestEntity testEntity = new TestEntity(stringField, longField, enumField);

        final Error expectedError = new Error("invalid_some_long", "some long must be positive");

        final Optional<Error> result = testEntity.testMethod(0);

        assertEquals(Optional.of(expectedError), result);
    }

    @Test
    void testMethod_ReturnEmptyOptional() {
        final String stringField = TestDataGen.randomShortString();
        final Long longField = TestDataGen.randomId();
        final TestEnum enumField = TestDataGen.randomEnum(TestEnum.class);

        final TestEntity testEntity = new TestEntity(stringField, longField, enumField);

        final Optional<Error> result = testEntity.testMethod(TestDataGen.randomId());

        assertTrue(result.isEmpty());
    }
}
