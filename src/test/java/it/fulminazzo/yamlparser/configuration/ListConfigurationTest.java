package it.fulminazzo.yamlparser.configuration;

import it.fulminazzo.yamlparser.parsers.CallableYAMLParser;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ListConfigurationTest {
    private static File file;

    @BeforeAll
    static void setAllUp() {
        try {
            file = new File("build/resources/test/list-tests.yml");
            if (file.exists()) FileUtils.deleteFile(file);
            FileUtils.createNewFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testNoIndexLists() {
        FileConfiguration.addParsers(new CallableYAMLParser<>(Server.class, s -> new Server()));

        List<Server> expected = new ArrayList<>(Arrays.asList(
                createServer("127.0.0.1", 8080),
                createServer("localhost", 51704),
                createServer("fulminazzo.it", 443)
        ));

        FileConfiguration configuration = new FileConfiguration(file);
        configuration.setList("servers", expected);
        configuration.save();

        configuration = new FileConfiguration(file);
        List<Server> actual = configuration.getList("servers", Server.class);

        assertEquals(expected, actual);
    }

    private Server createServer(String ip, int port) {
        Server server = new Server();
        server.ip = ip;
        server.port = port;
        return server;
    }

    private static class Server {
        String ip;
        int port;

        @Override
        public boolean equals(Object o) {
            if (o instanceof Server) {
                Server server = (Server) o;
                return Objects.equals(this.ip, server.ip) && this.port == server.port;
            }
            return false;
        }
    }
}