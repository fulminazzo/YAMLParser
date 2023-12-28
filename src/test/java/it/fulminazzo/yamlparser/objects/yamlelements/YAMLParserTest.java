package it.fulminazzo.yamlparser.objects.yamlelements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class YAMLParserTest {
    private YAMLParser<?> yamlParser;

    @BeforeEach
    public void setUp() {
        yamlParser = new UUIDYAMLParser();
    }

    @Test
    public void testYAMLParserEqualsSameYAMLParser() {
        assertEquals(yamlParser, new UUIDYAMLParser());
    }

    @Test
    public void testYAMLParserNotEqualsDifferentYAMLParser() {
        assertNotEquals(yamlParser, new ArrayYAMLParser<>());
    }

    @Test
    public void testYAMLParserNotEqualsYAMLInnerClass() {
        assertNotEquals(yamlParser, UUID.class);
    }

    @Test
    public void testYAMLParserNotEqualsYAMLClass() {
        assertNotEquals(yamlParser, UUIDYAMLParser.class);
    }

    @Test
    public void testYAMLParserNotEqualsGenericObject() {
        assertNotEquals(yamlParser, "Generic string");
    }
}