package com.margic.lookup;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Generate {

  private final Properties props;

  public Generate(Properties props){
    log.info("creating new producer");
    this.props = props;
  }

  public void produce(){
    Producer<String, String> producer = new KafkaProducer<>(props);
    String topic = props.getProperty("topic");
    producer.send(new ProducerRecord(topic, "key", "value"));
    producer.close();
  }
}
