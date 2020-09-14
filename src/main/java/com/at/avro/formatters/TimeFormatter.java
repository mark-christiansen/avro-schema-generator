package com.at.avro.formatters;

import com.at.avro.config.FormatterConfig;
import com.at.avro.types.Time;

public class TimeFormatter implements Formatter<Time> {
    @Override
    public String toJson(Time time, FormatterConfig config) {
        String template = "{ \"type\":\"%s\", \"logicalType\":\"%s\"}".replaceAll(":", config.colon());
        return String.format(template, time.getPrimitiveType(), time.getLogicalType());
    }
}
