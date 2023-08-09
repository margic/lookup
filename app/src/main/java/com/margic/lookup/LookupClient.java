package com.margic.lookup;

import java.util.Properties;

import com.margic.lookup.proto.LookupServiceGrpc;
import com.margic.lookup.proto.Lookup.LookupRequest;
import com.margic.lookup.proto.Lookup.LookupResponse;
import com.margic.lookup.proto.LookupServiceGrpc.LookupServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LookupClient {
  private final String grpcHost;
  private final int grpcPort;

  public LookupClient(Properties props) {
    this.grpcHost = props.getProperty("grpc.host");
    this.grpcPort = Integer.parseInt(props.getProperty("grpc.port"));
  }

  public void run() throws Exception {
    log.info("Run client");
    ManagedChannel channel = ManagedChannelBuilder
        .forAddress(grpcHost, grpcPort)
        .usePlaintext()
        .build();
    LookupServiceBlockingStub stub = LookupServiceGrpc.newBlockingStub(channel);

    for (int i = 0; i < 10; i++) {

      long before = System.currentTimeMillis();
      LookupResponse response = stub.lookup(LookupRequest.newBuilder().setKey("key").build());

      log.info("response from service {}, time: {}", response, System.currentTimeMillis() - before);
    }
  }
}
