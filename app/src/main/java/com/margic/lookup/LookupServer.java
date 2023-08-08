package com.margic.lookup;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class LookupServer {
    private static final int PORT = 50051;
    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new LookupServiceImpl())
                .build()
                .start();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server == null) {
            return;
        }
        server.awaitTermination();
    }
  
}