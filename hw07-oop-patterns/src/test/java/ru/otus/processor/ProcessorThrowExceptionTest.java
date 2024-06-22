package ru.otus.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcessorThrowExceptionTest {
    private static Stream<Arguments> generateEvenLocalTime() {
        return Stream.of(Arguments.of(LocalTime.of(23, 1, 22)),
            Arguments.of(LocalTime.of(11, 15, 2)),
            Arguments.of(LocalTime.of(2, 59, 58)),
            Arguments.of(LocalTime.of(17, 0, 46))
        );
    }

    private static Stream<Arguments> generateOddLocalTime() {
        return Stream.of(Arguments.of(LocalTime.of(23, 1, 23)),
                Arguments.of(LocalTime.of(22, 1, 1)),
                Arguments.of(LocalTime.of(19, 43, 59)),
                Arguments.of(LocalTime.of(0, 0, 47))
        );
    }

    @ParameterizedTest
    @DisplayName("Тестируем выбрасывание исключения при чётных секундах")
    @MethodSource("generateEvenLocalTime")
    public void throwExceptionByEvenSecTest(LocalTime localTime) {
        var timeProvider = mock(TimeProvider.class);
        when(timeProvider.getTime()).thenReturn(localTime);

        var processor = new ProcessorThrowException(timeProvider);
        assertThrows(RuntimeException.class, () -> processor.process(null));
    }

    @ParameterizedTest
    @DisplayName("Тестируем отсутствие исключение при нечётных секундах")
    @MethodSource("generateOddLocalTime")
    public void noExceptionByOddSecTest(LocalTime localTime) {
        var timeProvider = mock(TimeProvider.class);
        when(timeProvider.getTime()).thenReturn(localTime);

        var processor = new ProcessorThrowException(timeProvider);
        assertDoesNotThrow(() -> processor.process(null));
    }
}
