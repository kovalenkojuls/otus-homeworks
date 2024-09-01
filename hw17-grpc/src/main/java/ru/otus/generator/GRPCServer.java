package ru.otus.generator;

import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.generator.service.RemoteGeneratorServiceImpl;

import java.io.IOException;

public class GRPCServer {
    private static final Logger logger = LoggerFactory.getLogger(GRPCServer.class);

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws InterruptedException, IOException {
        var remoteGeneratorService = new RemoteGeneratorServiceImpl();

        var server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(remoteGeneratorService)
                .build();

        server.start();
        logger.info("server waiting for client connections...");
        server.awaitTermination();
    }
}
