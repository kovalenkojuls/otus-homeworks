package ru.otus.processor;

import java.time.LocalTime;

public interface TimeProvider {
    LocalTime getTime();
}
