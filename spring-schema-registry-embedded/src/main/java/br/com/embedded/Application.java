package br.com.embedded;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	public static void main(final String[] args) {
		run(args);
		SpringApplication.run(Application.class, args);
	}

	public static void run(final String[] args) {
		
		final Options options = new Options();
		
        final Option partitions = new Option("p", "topic-partitions", true, "the number of partitions per topic");
        partitions.setType(Integer.class);
        partitions.setArgs(1);
        partitions.setRequired(false);
        options.addOption(partitions);

        final Option brokersPort = new Option("P", "broker-port", true, "the number of port broker");
        brokersPort.setType(Integer.class);
        brokersPort.setArgs(1);
        brokersPort.setRequired(false);
        options.addOption(brokersPort);

        final Option brokers = new Option("b", "brokers", true, "the number of brokers");
        brokers.setType(Integer.class);
        brokers.setArgs(1);
        brokers.setRequired(false);
        options.addOption(brokers);

        final Option topics = new Option("t", "topics", true, "the topics to create");
        topics.setType(String.class);
        topics.setArgs(1);
        topics.setRequired(false);
        options.addOption(topics);
      
        try {
        	final CommandLineParser parser = new DefaultParser();
        	parser.parse(options, args);
        } catch (final ParseException e) {
        	LOGGER.error(e.getMessage());
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("spring-schema-registry-embedded", options);
            System.exit(1);
        }
		
	}

}
