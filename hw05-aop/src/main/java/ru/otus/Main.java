package ru.otus;

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface testLogging = Ioc.createTestLogging();
        testLogging.calculation(123);
        testLogging.calculation2(123, 456);
        testLogging.calculation3(456, 789, "value");
    }
}