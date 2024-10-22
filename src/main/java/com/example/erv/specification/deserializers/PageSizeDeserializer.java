package com.example.erv.specification.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.example.erv.exceptions.ErvException;

import java.io.IOException;

public class PageSizeDeserializer extends JsonDeserializer<Integer> {
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Security measure to prevent querying a pageable query with more than maximum page size.
     */
    @Override
    public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            String pageSize = jsonParser.getText();
            int pageSizeValue = Integer.parseInt(pageSize);
            if (pageSizeValue > MAX_PAGE_SIZE) throw new ErvException("Maximum page size exceeded!");
            return pageSizeValue;
        } catch (NumberFormatException | ErvException e) {
            throw new IllegalArgumentException("Invalid pageSize: " + jsonParser.getText());
        }
    }
}
