package ru.otus.atm.banknote;

import java.util.List;

public interface AllDenomination {
    public List<Integer> getAllDenomination();
    public boolean checkAmount(int amount);
}
