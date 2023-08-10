package com.margic.lookup;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Generate {

  private final Properties props;
  private int start = 1;
  private int count = 1;
  private String update;

  public Generate(Properties props){
    log.info("creating new producer");
    this.props = props;
    if (this.props.getProperty("start") != null){
      this.start = Integer.parseInt(this.props.getProperty("start"));
    }
    if (this.props.getProperty("count") != null){
      this.count = Integer.parseInt(this.props.getProperty("count"));
    }
    if (this.props.getProperty("update") != null){
      this.update = this.props.getProperty("update");
    }
  }

  public void produce(){
    Producer<String, String> producer = new KafkaProducer<>(props);
    int end = start + count;
    ProducerRecord<String, String> record;
    for (int i = start; i < end; i++){
      record = new ProducerRecord<String,String>(props.getProperty("topic"), Integer.toString(i), "Value: " + i + " update: " + update);
      // log.info("producing {}", record);
      producer.send(record);
    }
    producer.close();
  }
}
