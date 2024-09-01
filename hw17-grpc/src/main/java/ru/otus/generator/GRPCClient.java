package ru.otus.generator;

import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.generator.service.GeneratorResponseObserver;
import java.util.concurrent.CountDownLatch;

public class GRPCClient {
    private static final Logger logger = LoggerFactory.getLogger(GRPCServer.class);

    private static final String SERVER_HOST = GRPCServer.SERVER_HOST;
    private static final int SERVER_PORT = GRPCServer.SERVER_PORT;

    public static void main(String[] args) throws InterruptedException {
        var generatorResponseObserver = createGeneratorResponseObserver();
        processServerResponses(generatorResponseObserver);
        generatorResponseObserver.getLatch().await();
    }

    private static void processServerResponses(GeneratorResponseObserver generatorResponseObserver) throws InterruptedException {
        int currentValue = 0;
        for (int i = 0; i < 50; i++) {
            currentValue = currentValue + generatorResponseObserver.getLastNumberFromServer() + 1;
            logger.info("currentValue = {}", currentValue);

            Thread.sleep(1000);
        }
    }

    private static GeneratorResponseObserver createGeneratorResponseObserver() {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var stub = RemoteGeneratorServiceGrpc.newStub(channel);
        NumberRequest request = NumberRequest.newBuilder()
                .setFirstValue(1)
                .setLastValue(30)
                .build();

        var latch = new CountDownLatch(1);
        var generatorResponseObserver = new GeneratorResponseObserver(latch);
        stub.getNumbers(request, generatorResponseObserver);

        return generatorResponseObserver;
    }
}
