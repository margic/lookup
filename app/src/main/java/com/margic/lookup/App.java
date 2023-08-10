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
        options.addOption("m", "mode", true, "set the launch mode of the app; client server or generator");
        options.addOption("s", "startkey", true, "start of key range");
        options.addOption("c", "count", true, "count");

        try {
            // parse the command line arguments
            CommandLine cli = parser.parse(options, args);
            
            // validate that block-size has been set
            if (cli.hasOption("m")) {
                // print the value of mode
                String mode = cli.getOptionValue("m");
                log.info("Mode set to {}", mode);
                Properties props = Config.loadConfig("app.properties");
                switch (mode) {
                    case "client":
                        log.info("Running in client mode");
                        client(props);
                        break;
                
                    case "generate":
                        log.info("Running in generate mode");
                        // add the start and count args to the props for the generate function
                        if (cli.hasOption("s")){
                            props.setProperty("start", cli.getOptionValue("s"));
                        }
                        if (cli.hasOption("c")){
                            props.setProperty("count", cli.getOptionValue("c"));
                        }
                        generate(props);
                        break;

                    case "server":                        
                        log.info("Running in server mode");
                        serve(props);
                        break;
                    
                    default:
                        log.error("invalid mode - {}", mode);
                        break;
                }
            } else {
                log.info("no mode set");
            }
        } catch (Exception exp) {
            log.error("Unexpected exception", exp);
        }
    }

    static void generate(Properties props) throws Exception {
        Generate gen = new Generate(props);
        gen.produce();
    }

    static void serve(Properties props) throws Exception {
        LookupServer server = new LookupServer(props);
        server.start();
        server.blockUntilShutdown();
    }

    static void client(Properties props) throws Exception {
        LookupClient client = new LookupClient(props);
        client.run();
    }
}
