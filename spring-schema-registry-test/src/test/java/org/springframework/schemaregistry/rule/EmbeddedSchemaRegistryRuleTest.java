package org.springframework.schemaregistry.rule;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedSchemaRegistryRuleTest {

    private EmbeddedSchemaRegistryRule embeddedSchemaRegistryRule;

    @Test
    public void testCreateEmbeddedSchemaRegistryServer() throws IOException {
        // WHEN
        this.embeddedSchemaRegistryRule = new EmbeddedSchemaRegistryRule();

        // THEN
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getPort()).isEqualTo(8081);
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getKafkaConnectionUrl())
                .isEqualTo("localhost:2181");
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getServer()).isNull();
    }

    @Test
    public void testCreateEmbeddedSchemaRegistryServerWithoutPort() throws IOException {
        // WHEN
        this.embeddedSchemaRegistryRule = new EmbeddedSchemaRegistryRule(8981);

        // THEN
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getPort()).isEqualTo(8981);
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getKafkaConnectionUrl())
                .isEqualTo("localhost:2181");
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getServer()).isNull();
    }

    @Test
    public void testCreateEmbeddedSchemaRegistryServerWithoutPortAndKafkaUrl() throws IOException {
        // WHEN
        this.embeddedSchemaRegistryRule = new EmbeddedSchemaRegistryRule("localhost:2989");

        // THEN
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getPort()).isEqualTo(8081);
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getKafkaConnectionUrl())
                .isEqualTo("localhost:2989");
        assertThat(this.embeddedSchemaRegistryRule.embeddedSchemaRegistryServer.getServer()).isNull();
    }
}
