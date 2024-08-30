package ru.otus.generator.service;

import io.grpc.stub.StreamObserver;
import ru.otus.generator.NumberRequest;
import ru.otus.generator.NumberResponse;
import ru.otus.generator.RemoteGeneratorServiceGrpc;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class RemoteGeneratorServiceImpl extends RemoteGeneratorServiceGrpc.RemoteGeneratorServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(RemoteGeneratorServiceImpl.class);

    @Override
    public void getNumbers(NumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        int firstValue = request.getFirstValue();
        int lastValue = request.getLastValue();

        for (int i = firstValue + 1; i <= lastValue; i++) {
            logger.info("Generate the number: {}", i);
            NumberResponse response = NumberResponse
                    .newBuilder()
                    .setNumber(i)
                    .build();

            responseObserver.onNext(response);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }
}
