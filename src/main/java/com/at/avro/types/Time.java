package com.at.avro.types;

import com.at.avro.config.AvroConfig;
import schemacrawler.schema.Column;

public class Time extends Type {

    private final String logicalType;
    private final String javaClass;

    public Time(Column column, AvroConfig config) {
        super(config.isTimesInMicroseconds() ? "long" : "int");
        this.logicalType = config.getDefaultTimeLogicalType();
        this.javaClass = java.sql.Time.class.getCanonicalName();
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
