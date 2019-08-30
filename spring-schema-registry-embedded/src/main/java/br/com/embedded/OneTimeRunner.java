package br.com.embedded;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
class OneTimeRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(OneTimeRunner.class);
	
	@Autowired
    private ConfigurableApplicationContext context;

	@Override
	public void run(final String... args) throws Exception {

		final Options options = new Options();

		final Option partitions = new Option(null, "topic-partitions", true, "the number of partitions per topic");
		partitions.setType(Integer.class);
		partitions.setArgs(1);
		partitions.setRequired(false);
		options.addOption(partitions);

		final Option brokerPort = new Option(null, "broker-port", true, "the number of port broker");
		brokerPort.setType(Integer.class);
		brokerPort.setArgs(1);
		brokerPort.setRequired(false);
		options.addOption(brokerPort);

		final Option schemaRegistryPort = new Option(null, "schema-registry-port", true, "the number of port schema registry");
		schemaRegistryPort.setType(Integer.class);
		schemaRegistryPort.setArgs(1);
		schemaRegistryPort.setRequired(false);
		options.addOption(schemaRegistryPort);

		final Option brokers = new Option(null, "brokers", true, "the number of brokers");
		brokers.setType(Integer.class);
		brokers.setArgs(1);
		brokers.setRequired(false);
		options.addOption(brokers);

		final Option topics = new Option(null, "topics", true, "the topics to create");
		topics.setType(String.class);
		topics.setArgs(1);
		topics.setRequired(false);
		options.addOption(topics);

		final Option help = new Option(null, "help", true, "print this help");
		help.setType(String.class);
		help.setArgs(0);
		help.setRequired(false);
		options.addOption(help);

		final HelpFormatter formatter = new HelpFormatter();

		try {
			final CommandLineParser parser = new DefaultParser();
			final CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("help")) {
				formatter.printHelp("spring-schema-registry-embedded", options);
				SpringApplication.exit(context);
			}
		} catch (final ParseException e) {
			LOGGER.error(e.getMessage());
			formatter.printHelp("spring-schema-registry-embedded", options);
			SpringApplication.exit(context);
		}

	}

}
