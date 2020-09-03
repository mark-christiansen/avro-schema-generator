package com.at.avro.formatters;

import com.at.avro.config.FormatterConfig;
import com.at.avro.types.Timestamp;

public class TimestampFormatter implements Formatter<Timestamp> {
    @Override
    public String toJson(Timestamp date, FormatterConfig config) {
        String template = "{ \"type\":\"%s\", \"logicalType\":\"%s\"}".replaceAll(":", config.colon());
        return String.format(template, date.getPrimitiveType(), date.getLogicalType());
    }
}
