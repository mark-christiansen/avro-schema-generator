package com.at.avro.types;

import com.at.avro.config.AvroConfig;
import schemacrawler.schema.Column;

public class Timestamp extends Type {

    private final String logicalType;
    private final String javaClass;

    public Timestamp(Column column, AvroConfig config) {
        super("long");
        this.logicalType = config.getDefaultTimestampLogicalType();
        this.javaClass = config.getDateTypeClass().getCanonicalName();
    }

    public String getLogicalType() {
        return logicalType;
    }

    public String getJavaClass() {
        return javaClass;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + getLogicalType();
    }
}
