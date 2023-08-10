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
  private int start = 1;
  private int count = 1;

  public LookupClient(Properties props) {
    this.grpcHost = props.getProperty("grpc.host");
    this.grpcPort = Integer.parseInt(props.getProperty("grpc.port"));
    if (props.getProperty("start") != null){
      this.start = Integer.parseInt(props.getProperty("start"));
    }
    if (props.getProperty("count") != null){
      this.count = Integer.parseInt(props.getProperty("count"));
    }
  }

  public void run() throws Exception {
    log.info("Run client");
    ManagedChannel channel = ManagedChannelBuilder
        .forAddress(grpcHost, grpcPort)
        .usePlaintext()
        .build();
    LookupServiceBlockingStub stub = LookupServiceGrpc.newBlockingStub(channel);

    int end = start + count;
    for (int i = start; i < end; i++) {

      long before = System.currentTimeMillis();
      LookupResponse response = stub.lookup(LookupRequest.newBuilder().setKey(Integer.toString(i)).build());

      log.info("response from service {}, time: {}", response, System.currentTimeMillis() - before);
    }
  }
}
