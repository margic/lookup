package com.margic.lookup;


import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
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
        options.addOption("g", "generate", true, "Stream mock lookup data");
    
        try {
            // parse the command line arguments
            CommandLine cli = parser.parse(options, args);

            // validate that block-size has been set
            if (cli.hasOption("g")) {
                // print the value of block-size
                log.info("Running in generate mode");
                String generateCount = cli.getOptionValue("g");
                log.info("Generating {} records", generateCount);
                Properties props = Config.loadConfig("app.properties");
                Generate gen = new Generate(props);
                gen.produce();
            } else {
                log.info("Running in service mode");
                LookupServer server = new LookupServer();
                server.start();
                server.blockUntilShutdown();
            }
        } catch (Exception exp) {
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
