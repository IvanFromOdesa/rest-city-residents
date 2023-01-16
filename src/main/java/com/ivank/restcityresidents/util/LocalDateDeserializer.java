package com.ivank.restcityresidents.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ivank.restcityresidents.exception.LocalDateParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Custom deserializer for LocalDate (you may set your own pattern - if you decide to do so,
 * do not forget about db date store pattern either). Used mainly to throw custom exception,
 * if deserialization for LocalDate failed.
 */
public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateDeserializer() {
        this(null);
    }

    public LocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        LocalDate parsedLocalDate;
        try {
            parsedLocalDate = LocalDate.parse(node.asText(), dateFormat);
        } catch (DateTimeParseException e) {
            throw new LocalDateParseException("LocalDate field deserialization failed! Either empty or invalid pattern!");
        }
        return parsedLocalDate;
    }
}
