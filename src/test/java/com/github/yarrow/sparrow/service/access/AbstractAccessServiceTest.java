package com.github.yarrow.sparrow.service.access;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractAccessServiceTest extends MockMvcAbstractTest {

    private StringAccessServiceMock stringAccessServiceMock;

    @BeforeEach
    public void setUp() {
        stringAccessServiceMock = new StringAccessServiceMock();
    }

    @Test
    public void stringIsReturned() {
        //Arrange
        stringAccessServiceMock.add("expected");

        //Act
        var actualString = stringAccessServiceMock.getPresentOrThrow("expected");

        //Assert
        Assertions.assertThat(actualString).isEqualTo("expected");
    }

    @Test
    public void securedStringIsReturned() {
        //Arrange
        stringAccessServiceMock.addSecured("expected");

        //Act
        var actualString = stringAccessServiceMock.getPresentOrThrowSecured("expected");

        //Assert
        Assertions.assertThat(actualString).isEqualTo("expected");
    }

    @Test
    public void nonexistentStringLeadsToThrow() {
        //Assert
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrow("non-existent"))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }

    @Test
    public void nonexistentSecuredStringLeadsToThrow() {
        //Assert
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrowSecured("non-existent"))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }

    @Test
    public void stringListIsReturned() {
        //Arrange
        stringAccessServiceMock.add("expected-1");
        stringAccessServiceMock.add("expected-2");

        //Act
        var strings = stringAccessServiceMock.getPresentOrThrow(List.of("expected-1", "expected-2"));

        //Assert
        Assertions.assertThat(strings).containsExactlyInAnyOrder("expected-1", "expected-2");
    }

    @Test
    public void securedStringListIsReturned() {
        //Arrange
        stringAccessServiceMock.addSecured("expected-1");
        stringAccessServiceMock.addSecured("expected-2");

        //Act
        var strings = stringAccessServiceMock.getPresentOrThrowSecured(List.of("expected-1", "expected-2"));

        //Assert
        Assertions.assertThat(strings).containsExactlyInAnyOrder("expected-1", "expected-2");
    }

    @Test
    public void nonexistentStringListLeadsToThrow() {
        //Act + Assert
        var strings = List.of("non-existent-1", "non-existent-2");
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrow(strings))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }

    @Test
    public void nonexistentSecuredStringListLeadsToThrow() {
        //Act + Assert
        var strings = List.of("non-existent-1", "non-existent-2");
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrowSecured(strings))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }

    @Test
    public void partlyExistentStringListLeadsToThrow() {
        //Arrange
        stringAccessServiceMock.addSecured("expected");

        //Act + Assert
        var strings = List.of("expected", "non-existent");
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrow(strings))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }

    @Test
    public void partlyExistentSecuredStringListLeadsToThrow() {
        //Arrange
        stringAccessServiceMock.addSecured("expected");

        //Act + Assert
        var strings = List.of("expected", "non-existent");
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrowSecured(strings))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }

    @Test
    public void nullInArgumentLeadsToCorrectExceptionThrow() {
        //Arrange
        stringAccessServiceMock.addSecured("not-found-exception-must-be-threw");

        //Act + Assert
        var strings = new ArrayList<String>();
        strings.add(null);
        Assertions.assertThatThrownBy(() -> stringAccessServiceMock.getPresentOrThrowSecured(strings))
                .isExactlyInstanceOf(StringNotFoundException.class);
    }
}
