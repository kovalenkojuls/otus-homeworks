package ru.otus.atm.banknote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllDenominationRUB implements AllDenomination {
    private List<Integer> allDenominationRUB = new ArrayList<>();

    public AllDenominationRUB() {
        allDenominationRUB = List.of(5_000, 1_000, 500, 100, 50, 10);
    }

    public List<Integer> getAllDenomination() {
        return Collections.unmodifiableList(allDenominationRUB);
    }

    public boolean checkAmount(int amount) {
        return amount % 10 == 0;
    }
}
