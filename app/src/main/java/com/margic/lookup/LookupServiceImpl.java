package com.margic.lookup;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import com.margic.lookup.proto.Lookup;
import com.margic.lookup.proto.LookupServiceGrpc;

import io.grpc.stub.StreamObserver;

@Slf4j
public class LookupServiceImpl extends LookupServiceGrpc.LookupServiceImplBase {

    private ReadOnlyKeyValueStore<String, String> lookupView;

    public LookupServiceImpl(Properties props){
        String topic = props.getProperty("topic");

        // create topology to fetch data from kafka
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        builder.globalTable(topic, Consumed.with(stringSerde, stringSerde),
                        Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("LOOKUP")
                                .withKeySerde(stringSerde)
                                .withValueSerde(stringSerde));                       
                 
        KafkaStreams streams = new KafkaStreams(builder.build(), props);                                
        streams.start();
        this.lookupView = streams.store(StoreQueryParameters.fromNameAndType("LOOKUP", QueryableStoreTypes.keyValueStore()));
    }

    @Override
    public void lookup(
            Lookup.LookupRequest request,
            StreamObserver<Lookup.LookupResponse> responseObserver) {
        log.info("Handling lookup endpoint: {}", request.toString());

        String value = lookupView.get(request.getKey());
        if (value == null){
            value = "null";
        }
        Lookup.LookupResponse response = Lookup.LookupResponse.newBuilder()
                .setKey(request.getKey())
                .setValue(value).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}