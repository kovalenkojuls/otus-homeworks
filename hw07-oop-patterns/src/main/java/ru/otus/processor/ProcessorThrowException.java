package ru.otus.processor;

import ru.otus.model.Message;

public class ProcessorThrowException implements Processor {
    private final TimeProvider timeProvider;

    public ProcessorThrowException(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {
        if (timeProvider.getTime().getSecond() % 2 == 0) {
            throw new RuntimeException("The current number of seconds is even");
        }
        return message;
    }
}
