package com.margic.lookup;

import java.io.IOException;
import java.util.Properties;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class LookupServer {
    private final int port;

    public LookupServer(Properties props){
        port = Integer.parseInt(props.getProperty("grpc.port"));
    }

    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new LookupServiceImpl())
                .addService(ProtoReflectionService.newInstance())
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