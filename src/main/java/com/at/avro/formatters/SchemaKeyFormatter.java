package com.at.avro.formatters;

import com.at.avro.AvroField;
import com.at.avro.AvroSchema;
import com.at.avro.config.FormatterConfig;

import java.util.List;

public class SchemaKeyFormatter extends SchemaFormatter {

    private static final String NAME_SUFFIX = "_KEY";

    @Override
    protected String getRecordName(AvroSchema avroSchema, FormatterConfig config) {
        String name = avroSchema.getName() + NAME_SUFFIX;
        return config.lowerCaseNames() ? name.toLowerCase() : name;
    }

    @Override
    protected List<AvroField> getFields(AvroSchema avroSchema) {
        return avroSchema.getKeys();
    }
}
