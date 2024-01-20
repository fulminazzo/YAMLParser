package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.yamlparser.parsers.ArrayYAMLParser;
import it.fulminazzo.yamlparser.parsers.UUIDYAMLParser;
import it.fulminazzo.yamlparser.parsers.YAMLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class YAMLParserTest {
    private YAMLParser<?> yamlParser;

    @BeforeEach
    void setUp() {
        yamlParser = new UUIDYAMLParser();
    }

    @Test
    void testYAMLParserEqualsSameYAMLParser() {
        assertEquals(yamlParser, new UUIDYAMLParser());
    }

    @Test
    void testYAMLParserNotEqualsDifferentYAMLParser() {
        assertNotEquals(yamlParser, new ArrayYAMLParser<>());
    }

    @Test
    void testYAMLParserNotEqualsYAMLInnerClass() {
        assertNotEquals(yamlParser, UUID.class);
    }

    @Test
    void testYAMLParserNotEqualsYAMLClass() {
        assertNotEquals(yamlParser, UUIDYAMLParser.class);
    }

    @Test
    void testYAMLParserNotEqualsGenericObject() {
        assertNotEquals(yamlParser, "Generic string");
    }
}