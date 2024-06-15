package ru.otus.atm;

import ru.otus.atm.banknote.Banknote;

import java.util.List;

public interface ATMService {
    public void putBanknotes(List<Banknote> banknotes);
    public List<Banknote> getBanknotes(int amount);
    public void printBanknoteBoxes();
    public int getAmount();
}
