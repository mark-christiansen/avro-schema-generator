package com.at.avro.formatters;

import com.at.avro.AvroField;
import com.at.avro.AvroSchema;

import java.util.List;

public class SchemaKeyFormatter extends SchemaFormatter {

    private static final String NAME_SUFFIX = "_KEY";

    @Override
    protected String getRecordName(AvroSchema avroSchema) {
        return avroSchema.getName() + NAME_SUFFIX;
    }

    @Override
    protected List<AvroField> getFields(AvroSchema avroSchema) {
        return avroSchema.getKeys();
    }
}
