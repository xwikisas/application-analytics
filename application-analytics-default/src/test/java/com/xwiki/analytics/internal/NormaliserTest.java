package com.xwiki.analytics.internal;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonReader;

public abstract class NormaliserTest
{
    protected JsonNode readJSONS(String file) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = JsonReader.class.getResourceAsStream(file);
        JsonNode node = objectMapper.readTree(is);
        return node;
    }
}
