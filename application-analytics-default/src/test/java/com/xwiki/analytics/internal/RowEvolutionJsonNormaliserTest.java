package com.xwiki.analytics.internal;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link RowEvolutionJsonNormaliserTest}
 *
 * @version $Id$
 */
@ComponentTest
public class RowEvolutionJsonNormaliserTest
{
    @InjectMockComponents
    private RowEvolutionJsonNormaliser rowEvolutionJsonNormaliser;
    private static JsonNode node;

    private void readJSONS() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = JsonReader.class.getResourceAsStream("/tests.json");
        node = objectMapper.readTree(is);
    }

    @Test
    public void testNormalisation() throws IOException
    {
        readJSONS();
        assertEquals(node.get("RowEvolutionTestObjectResponse"),
            rowEvolutionJsonNormaliser.normaliseData(node.get("RowEvolutionTestObject").toString()));

    }
}
