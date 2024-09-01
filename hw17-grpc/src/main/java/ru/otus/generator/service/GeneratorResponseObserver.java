package ru.otus.generator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.stub.StreamObserver;
import ru.otus.generator.NumberResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorResponseObserver implements StreamObserver<NumberResponse> {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorResponseObserver.class);
    private final AtomicInteger lastNumberFromServer = new AtomicInteger(0);
    private final CountDownLatch latch;

    public GeneratorResponseObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(NumberResponse response) {
        logger.info("received from the server {}", response.getNumber());
        lastNumberFromServer.set(response.getNumber());
    }

    public int getLastNumberFromServer() {
        return lastNumberFromServer.getAndSet(0);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void onError(Throwable t) {
        logger.error(t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        logger.info("the server response has been processed");
        latch.countDown();
    }
}
