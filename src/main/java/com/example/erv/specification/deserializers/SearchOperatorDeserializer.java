package com.example.erv.specification.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.example.erv.specification.SearchOperator;

import java.io.IOException;

/**
 * Security measure to prevent exposing all possible search operators in a generic exception.
 */
public class SearchOperatorDeserializer extends JsonDeserializer<SearchOperator> {
    @Override
    public SearchOperator deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String operation = jsonParser.getText();

        for (SearchOperator searchOperator : SearchOperator.values()) {
            if (searchOperator.getOperation().equalsIgnoreCase(operation)) {
                return searchOperator;
            }
        }

        throw new IllegalArgumentException("Invalid SearchOperator: " + operation);
    }
}
