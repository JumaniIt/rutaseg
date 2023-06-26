package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.TestDataGen;
import com.jumani.rutaseg.dto.result.Error;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestEntityTest {

    private static final ZonedDateTime FIXED_CURRENT_DATE = ZonedDateTime.now();

    @Test
    void initialization_Ok() {
        final String stringField = TestDataGen.randomShortString();
        final Long longField = TestDataGen.randomId();
        final TestEnum enumField = TestDataGen.randomEnum(TestEnum.class);

        final TestEntity testEntity = new TestEntityForTest(stringField, longField, enumField);

        assertAll("Expected result",
                () -> assertEquals(stringField, testEntity.getStringField()),
                () -> assertEquals(longField, testEntity.getLongField()),
                () -> assertEquals(enumField, testEntity.getEnumField()),
                () -> assertEquals(FIXED_CURRENT_DATE, testEntity.getDateField())
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

    private static final class TestEntityForTest extends TestEntity {

        public TestEntityForTest(String stringField, Long longField, TestEnum enumField) {
            super(stringField, longField, enumField);
        }

        @Override
        public ZonedDateTime currentDateUTC() {
            return FIXED_CURRENT_DATE;
        }
    }
}
