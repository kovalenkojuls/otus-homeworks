package ru.otus.atm.banknote;

import java.util.ArrayList;
import java.util.List;

public class BanknoteBox {
    private final int boxDenomination;
    private List<Banknote> banknotes = new ArrayList<>();

    public BanknoteBox(int boxDenomination) {
        this.boxDenomination = boxDenomination;
    }

    public int getBoxDenomination() {
        return boxDenomination;
    }

    public int getBanknoteCount() {
        return banknotes.size();
    }

    public void addBanknote(Banknote banknote) {
        banknotes.add(banknote);
    }

    public List<Banknote> getBanknotes(int count) {
        if (count > banknotes.size()) {
            throw new RuntimeException("Wrong count of banknotes");
        }

        List<Banknote> banknotesForReturn = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            banknotesForReturn.add(banknotes.get(i));
        }

        banknotes = banknotes.subList(count - 1, banknotes.size() - 1);
        return banknotesForReturn;
    }
}
