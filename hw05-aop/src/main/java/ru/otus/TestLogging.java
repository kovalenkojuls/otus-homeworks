package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.Log;

public class TestLogging implements TestLoggingInterface {
    private static final Logger logger = LoggerFactory.getLogger(TestLogging.class);

    @Log
    @Override
    public void calculation(int param) {}

    @Override
    public void calculation2(int param1, int param2) {}

    @Log
    @Override
    public void calculation3(int param1, int param2, String param3) {}

    @Override
    public String toString() {
        return "TestLogging{}";
    }
}