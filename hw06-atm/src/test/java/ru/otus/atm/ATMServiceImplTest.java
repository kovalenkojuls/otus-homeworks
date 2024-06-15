package ru.otus.atm;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.atm.banknote.AllDenomination;
import ru.otus.atm.banknote.Banknote;
import java.util.List;
import java.util.stream.Stream;

@DisplayName("Сервис ATMServiceImplTest должен ")
public class ATMServiceImplTest {
    private ATMService atmService;

    private static List<List<Banknote>> getBanknoteList() {
        return List.of(List.of(
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10)
        ), List.of(
                new Banknote(100),
                new Banknote(5_000),
                new Banknote(5_000)
        ), List.of(
                new Banknote(5_000),
                new Banknote(5_000),
                new Banknote(5_000),
                new Banknote(1_000),
                new Banknote(500),
                new Banknote(500),
                new Banknote(100),
                new Banknote(10)
        ), List.of(
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10),
                new Banknote(10)
        ));
    }

    private static Stream<Arguments> generateDataForPut() {
        List<List<Banknote>> banknoteList = getBanknoteList();
        return Stream.of(
                Arguments.of(banknoteList.get(0), 950, "Внесли 950"),
                Arguments.of(banknoteList.get(1), 10_100, "Внесли 10_100"),
                Arguments.of(banknoteList.get(2), 17_110, "Внесли 17_110"),
                Arguments.of(banknoteList.get(3), 110, "Внесли 110")
        );
    }

    private static Stream<Arguments> generateDataForGet() {
        List<List<Banknote>> banknoteList = getBanknoteList();
        return Stream.of(
                Arguments.of(banknoteList.get(0), 410, 540, "Внесли 950, сняли 410, остаток 540"),
                Arguments.of(banknoteList.get(1), 5_000, 5_100, "Внесли 10_100, сняли 5_000, остаток 5_100"),
                Arguments.of(banknoteList.get(2), 7_010, 10_100, "Внесли 17_110, сняли 7_010, остаток 10_100"),
                Arguments.of(banknoteList.get(3), 110, 0, "Внесли 110, сняли 110, остаток 0")
        );
    }

    @BeforeEach
    public void setUp() {
        AllDenomination allDenomination = mock(AllDenomination.class);
        var allDenominationList = List.of(5_000,1_000, 500, 100, 50,10);
        given(allDenomination.getAllDenomination()).willReturn(allDenominationList);
        atmService = new ATMServiceImpl(allDenomination);
    }


    @DisplayName("добавить в АТМ список банкнот и проверить итоговую сумму")
    @ParameterizedTest(name = "{2}")
    @MethodSource("generateDataForPut")
    public void putBanknotesAndCheckATMAmount(List<Banknote> banknotes, int amount, String prompt) {
        atmService.putBanknotes(banknotes);
        assertEquals(amount, atmService.getAmount());
    }

    @DisplayName("добавить в АТМ список банкнот, извлечь из АТМ некоторую сумму, проверить остаток")
    @ParameterizedTest(name = "{3}")
    @MethodSource("generateDataForGet")
    public void putBanknotesGetBanknotesAndCheckATMAmount(List<Banknote> banknotes, int get, int rest, String prompt) {
        atmService.putBanknotes(banknotes);
        atmService.getBanknotes(get);
        assertEquals(rest, atmService.getAmount());
    }

    @DisplayName("добавить в АТМ список банкнот, попытаться извлечь из АТМ сумму больше, чем внесли, получить исключение")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"Внесли 10, пытаемся снять 100"})
    void putBanknotesGetBanknotesAngGetException(String prompt) {
        atmService.putBanknotes(List.of(new Banknote(10)));
        assertThrows(RuntimeException.class, () -> atmService.getBanknotes(100));
    }

    @DisplayName("добавить в АТМ список банкнот, попытаться извлечь из АТМ сумму, некратную 10, получить исключение")
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"Внесли 100, пытаемся снять 11"})
    void putBanknotesGetBanknotesAngGetException2(String prompt) {
        atmService.putBanknotes(List.of(new Banknote(100)));
        assertThrows(RuntimeException.class, () -> atmService.getBanknotes(101));
    }
}
