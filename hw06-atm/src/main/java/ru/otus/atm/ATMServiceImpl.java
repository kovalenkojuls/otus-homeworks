package ru.otus.atm;

import ru.otus.atm.banknote.AllDenomination;
import ru.otus.atm.banknote.Banknote;
import ru.otus.atm.banknote.BanknoteBox;

import java.util.ArrayList;
import java.util.List;

public class ATMServiceImpl implements ATMService {
    private final List<BanknoteBox> banknoteBoxes = new ArrayList<>();
    private final AllDenomination allDenomination;

    public ATMServiceImpl(AllDenomination allDenomination) {
        this.allDenomination = allDenomination;

        for (Integer denomination: this.allDenomination.getAllDenomination()) {
            banknoteBoxes.add(new BanknoteBox(denomination));
        }
    }

    public void putBanknotes(List<Banknote> banknotes) {
        var allDenaminationList = allDenomination.getAllDenomination();
        for (Banknote banknote: banknotes) {
            if (!allDenaminationList.contains(banknote.denomination())) {
                throw new RuntimeException("Wrong banknote denomination");
            }

            for (BanknoteBox banknoteBox : banknoteBoxes) {
                if (banknoteBox.getBoxDenomination() == banknote.denomination()) {
                    banknoteBox.addBanknote(banknote);
                }
            }
        }
    }

    public List<Banknote> getBanknotes(int amount) {
        List<Banknote> banknotesForReturn = new ArrayList<>();

        if (amount > getAmount() || amount % 10 != 0) {
            throw new RuntimeException("Wrong amount");
        }

        for (BanknoteBox banknoteBox : banknoteBoxes) {
            boolean dividedByDenomination = amount / banknoteBox.getBoxDenomination() > 0;
            if (dividedByDenomination && banknoteBox.getBanknoteCount() > 0) {

                int countForReturn = Math.min(
                        amount / banknoteBox.getBoxDenomination(),
                        banknoteBox.getBanknoteCount()
                );

                banknotesForReturn.addAll(banknoteBox.getBanknotes(countForReturn));
                amount -= banknoteBox.getBoxDenomination() * countForReturn;
            }
        }

        if (amount != 0) {
            throw new RuntimeException("Wrong amount");
        }

        return banknotesForReturn;
    }

    public void printBanknoteBoxes() {
        for (BanknoteBox banknoteBox: banknoteBoxes) {
            System.out.println("Box " + banknoteBox.getBoxDenomination() +
                    ": " + banknoteBox.getBanknoteCount());
        }
    }

    public int getAmount() {
        int amount = 0;
        for (BanknoteBox banknoteBox: banknoteBoxes) {
            amount += banknoteBox.getBoxDenomination() * banknoteBox.getBanknoteCount();
        }

        return amount;
    }
}
