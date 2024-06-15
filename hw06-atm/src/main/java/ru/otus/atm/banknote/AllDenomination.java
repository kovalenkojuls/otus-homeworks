package ru.otus.atm.banknote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllDenomination {
    private List<Integer> allDenomination = new ArrayList<>();

    public AllDenomination() {
        allDenomination = List.of(5_000, 1_000, 500, 100, 50, 10);
    }

    public List<Integer> getAllDenomination() {

        return Collections.unmodifiableList(allDenomination);
    }
}
