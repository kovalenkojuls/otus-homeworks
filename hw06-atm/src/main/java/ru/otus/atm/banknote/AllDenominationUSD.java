package ru.otus.atm.banknote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllDenominationUSD implements AllDenomination {
    private List<Integer> allDenominationRUB = new ArrayList<>();

    public AllDenominationUSD() {
        allDenominationRUB = List.of(100, 50, 20, 10, 5, 2, 1);
    }

    public List<Integer> getAllDenomination() {
        return Collections.unmodifiableList(allDenominationRUB);
    }

    public boolean checkAmount(int amount) {
        return true;
    }
}
