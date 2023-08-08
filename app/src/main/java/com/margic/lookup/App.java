package com.margic.lookup;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] args) {
        log.info("Starting");

        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // validate that block-size has been set
            if (line.hasOption("a")) {
                // print the value of block-size
                log.info(line.getOptionValue("a"));
            }
        } catch (ParseException exp) {
            log.error("Unexpected exception", exp);
        }

    }

    static Topology buildTopology(String inputTopic, String outputTopic) {
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream(inputTopic, Consumed.with(stringSerde, stringSerde))
                .to(outputTopic, Produced.with(stringSerde, stringSerde));
        return builder.build();
    }

}
