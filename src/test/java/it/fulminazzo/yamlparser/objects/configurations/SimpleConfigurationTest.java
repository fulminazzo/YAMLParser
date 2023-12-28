package it.fulminazzo.yamlparser.objects.configurations;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SimpleConfigurationTest {

    @Test
    public void testEmptySimpleConfiguration() {
        SimpleConfiguration simpleConfiguration = new SimpleConfiguration();
        SimpleConfiguration expected = new SimpleConfiguration("", new LinkedHashMap<>());
        assertEquals(simpleConfiguration, expected);
    }
}