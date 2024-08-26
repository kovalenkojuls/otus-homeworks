package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HwThreads {
    private static final Logger logger = LoggerFactory.getLogger(HwThreads.class);
    private boolean firstIteration = true;
    private String lastThreadPrint;

    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 10;

    public static void main(String[] args) throws InterruptedException {
        var hwThreads = new HwThreads();

        var thread1 = new Thread(hwThreads::printNumbers);
        thread1.setName("Thread-1");

        var thread2 = new Thread(hwThreads::printNumbers);
        thread2.setName("Thread-2");

        thread2.start();
        thread1.start();

        thread1.join();
        thread2.join();
    }

    private synchronized void printNumbers() {
        int number = MIN_VALUE;
        boolean isIncrease = true;

        while (firstIteration && Thread.currentThread().getName().equals("Thread-2")) {
            waitWrapper();
        }
        firstIteration = false;

        while (!Thread.currentThread().isInterrupted()) {
            logger.info("{}: {}", Thread.currentThread().getName(), number);
            lastThreadPrint = Thread.currentThread().getName();
            notifyAll();

            while (Thread.currentThread().getName().equals(lastThreadPrint)) {
                waitWrapper();
            }

            number = isIncrease ? number + 1 : number - 1;
            if (number == MAX_VALUE || number == MIN_VALUE) {
                isIncrease = !isIncrease;
            }

            sleepWrapper(500);
        }
    }

    private void sleepWrapper(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.info("{} was interrupted", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
    }

    private void waitWrapper() {
        try {
            wait();
        } catch (InterruptedException e) {
            logger.info("{} was interrupted", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
    }
}
