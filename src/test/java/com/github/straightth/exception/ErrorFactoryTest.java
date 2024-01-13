package com.github.straightth.exception;

import com.github.straightth.MockMvcAbstractTest;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ErrorFactoryTest extends MockMvcAbstractTest {

    @Test
    public void errorFactoryIsBeingInitialized() {
        //Act & Assert
        var errorFactory = ErrorFactory.get();
        Assertions.assertThat(errorFactory).isNotNull();
    }

    @Test
    public void internalServerErrorIsCreatedCorrectly() {
        //Arrange
        var expectedError = new ApplicationError(
                "taskl.api.error.unknown",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unknown error",
                ApplicationError.Level.ERROR,
                "Unknown error",
                Map.of()
        );

        //Act
        var actualError = ErrorFactory.get().internalServerError();

        //Assert
        Assertions.assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    public void exceptionIsBeingThrown() {
        var throwableAssert = Assertions.assertThatThrownBy(() -> {
            throw ErrorFactory.get().internalServerError();
        });
        throwableAssert.isExactlyInstanceOf(ApplicationError.class);
    }
}