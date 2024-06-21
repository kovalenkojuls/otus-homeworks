package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerHistory;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.*;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        var complexProcessor = getComplexProcessor();

        var listenerPrinter = new ListenerPrinterConsole();
        var listenerHistory = new ListenerHistory();
        complexProcessor.addListener(listenerPrinter);
        complexProcessor.addListener(listenerHistory);

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11_FOR_SWAP")
                .field12("field12_FOR_SWAP")
                .field13(new ObjectForMessage(List.of("one","two", "three")))
                .build();

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        complexProcessor.removeListener(listenerPrinter);
        complexProcessor.removeListener(listenerHistory);
    }

    private static ComplexProcessor getComplexProcessor() {
        var processors = List.of(
                new ProcessorConcatFields(),
                new LoggerProcessor(new ProcessorUpperField10()),
                new ProcessorSwapField11Field12(),
                new ProcessorThrowException(LocalTime::now)
        );

        Consumer<Exception> errorHandler = (ex) -> {
            throw new RuntimeException(ex.getMessage());
        };

        return new ComplexProcessor(processors, errorHandler);
    }
}
