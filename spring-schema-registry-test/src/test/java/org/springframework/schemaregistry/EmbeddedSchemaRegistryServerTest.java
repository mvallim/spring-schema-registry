package org.springframework.schemaregistry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.schemaregistry.rule.EmbeddedSchemaRegistryRule;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddedSchemaRegistryServerTest {

    private EmbeddedSchemaRegistryServer embeddedSchemaRegistryServer;

    @Test
    public void testCreateEmbeddedSchemaRegistryServer() throws IOException {
        // WHEN
        this.embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer();

        // THEN
        assertThat(this.embeddedSchemaRegistryServer.getPort()).isPositive();
        assertThat(this.embeddedSchemaRegistryServer.getKafkaConnectionUrl())
                .isEqualTo("localhost:2181");
        assertThat(this.embeddedSchemaRegistryServer.getServer()).isNull();
    }

    @Test
    public void testCreateEmbeddedSchemaRegistryServerWithoutPort() throws IOException {
        // WHEN
        this.embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer(8981);

        // THEN
        assertThat(this.embeddedSchemaRegistryServer.getPort()).isEqualTo(8981);
        assertThat(this.embeddedSchemaRegistryServer.getKafkaConnectionUrl())
                .isEqualTo("localhost:2181");
        assertThat(this.embeddedSchemaRegistryServer.getServer()).isNull();
    }

    @Test
    public void testCreateEmbeddedSchemaRegistryServerWithoutPortAndKafkaUrl() throws IOException {
        // WHEN
        this.embeddedSchemaRegistryServer = new EmbeddedSchemaRegistryServer("localhost:2989");

        // THEN
        assertThat(this.embeddedSchemaRegistryServer.getPort()).isPositive();
        assertThat(this.embeddedSchemaRegistryServer.getKafkaConnectionUrl())
                .isEqualTo("localhost:2989");
        assertThat(this.embeddedSchemaRegistryServer.getServer()).isNull();
    }

    @Test
    public void testStopEmbeddedSchemaRegistryServerAndHaveProblem() throws Exception {
        // GIVEN
        this.embeddedSchemaRegistryServer = spy(new EmbeddedSchemaRegistryServer());
        doThrow(Exception.class).when(this.embeddedSchemaRegistryServer).stopServer();

        // WHEN
        Throwable thrown = catchThrowable(() -> this.embeddedSchemaRegistryServer.destroy());

        // THEN
        assertThat(thrown).isInstanceOf(Exception.class)
                .hasMessageContaining("Error shutdown embedded schema registry...");
    }
}
