package com.margic.lookup;

import lombok.extern.slf4j.Slf4j;
import com.margic.lookup.proto.Lookup;
import com.margic.lookup.proto.LookupServiceGrpc;

import io.grpc.stub.StreamObserver;

@Slf4j
public class LookupServiceImpl extends LookupServiceGrpc.LookupServiceImplBase {

    @Override
    public void lookup(
            Lookup.LookupRequest request,
            StreamObserver<Lookup.LookupResponse> responseObserver) {
        log.info("Handling lookup endpoint: {}", request.toString());


        String text = request.getKey() + " World";
        Lookup.LookupResponse response =
                Lookup.LookupResponse.newBuilder()
                        .setValue(text).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}