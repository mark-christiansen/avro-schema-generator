package com.at.avro.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.at.avro.AvroSchema;

import schemacrawler.schema.Table;

/**
 * Configuration that allows avro model tweaking. Model is then used to generate schemas.
 *
 * @author artur@callfire.com
 */
public class AvroConfig {

    private static final String TIME_MILLIS = "time-millis";
    private static final String TIME_MICROS = "time-micros";
    private static final String TIMESTAMP_MILLIS = "timestamp-millis";
    private static final String TIMESTAMP_MICROS = "timestamp-micros";

    private boolean representEnumsAsStrings = false;
    private boolean nullableTrueByDefault = false;
    private boolean allFieldsDefaultNull = false;
    private boolean useSqlCommentsAsDoc = false;
    private boolean timesInMicroseconds = false;
    private boolean timestampsInMicroseconds = false;

    private Class<?> decimalTypeClass = BigDecimal.class;
    private Class<?> dateTypeClass = LocalDateTime.class;

    private final String namespace;

    private Function<String, String> schemaNameMapper = tableName -> tableName;
    private Function<String, String> fieldNameMapper = columnName -> columnName;
    private Function<String, String> unknownTypeResolver = dbType -> { throw new IllegalArgumentException("unknown data type: " + dbType); };
    private BiConsumer<AvroSchema, Table> avroSchemaPostProcessor = (schema, table) -> {};

    public AvroConfig(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Provide custom schema name resolver function which takes DB table name as an input.
     * Table name is used by default.
     */
    public AvroConfig setSchemaNameMapper(Function<String, String> schemaNameMapper) {
        this.schemaNameMapper = schemaNameMapper;
        return this;
    }

    public Function<String, String> getSchemaNameMapper() {
        return schemaNameMapper;
    }

    /**
     * Provide custom field names resolver function which takes DB column name as an input.
     * Column name is used by default.
     */
    public AvroConfig setFieldNameMapper(Function<String, String> fieldNameMapper) {
        this.fieldNameMapper = fieldNameMapper;
        return this;
    }

    public Function<String, String> getFieldNameMapper() {
        return fieldNameMapper;
    }

    /**
     * Resolve 'enum' type to 'string' instead of 'enum'.
     */
    public AvroConfig setRepresentEnumsAsStrings(boolean representEnumsAsStrings) {
        this.representEnumsAsStrings = representEnumsAsStrings;
        return this;
    }

    public boolean representEnumsAsStrings() {
        return representEnumsAsStrings;
    }

    /**
     * Set to true to make all fields default to null. DB column definition is used by default.
     */
    public AvroConfig setAllFieldsDefaultNull(boolean allFieldsDefaultNull) {
        this.allFieldsDefaultNull = allFieldsDefaultNull;
        return this;
    }

    public boolean isAllFieldsDefaultNull() {
        return allFieldsDefaultNull;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * Set to true to make all fields nullable in avro schema. DB column definition is used by default.
     */
    public AvroConfig setNullableTrueByDefault(boolean nullableTrueByDefault) {
        this.nullableTrueByDefault = nullableTrueByDefault;
        return this;
    }

    public boolean isNullableTrueByDefault() {
        return nullableTrueByDefault;
    }

    /**
     * Sets a "java-class" property in this fields definition.
     * This might be used by avro java code generator to use the class you want for dates.
     */
    public AvroConfig setDateTypeClass(Class<?> dateTypeClass) {
        this.dateTypeClass = dateTypeClass;
        return this;
    }

    public Class<?> getDateTypeClass() {
        return dateTypeClass;
    }

    /**
     * Sets a "java-class" property in this fields definition.
     * This might be used by avro java code generator to use the class you want for decimals.
     */
    public AvroConfig setDecimalTypeClass(Class<?> decimalTypeClass) {
        this.decimalTypeClass = decimalTypeClass;
        return this;
    }

    public Class<?> getDecimalTypeClass() {
        return decimalTypeClass;
    }

    /**
     * Provide mapper for unknown db types. Throws IllegalArgumentException by default.
     * For example, if you want to default all unknown types to string:
     * <code>
     *     avroConfig.setUnknownTypeResolver(type -> "string")
     * </code>
     * IllegalArgumentException is thrown by default.
     **/
    public AvroConfig setUnknownTypeResolver(Function<String, String> unknownTypeResolver) {
        this.unknownTypeResolver = unknownTypeResolver;
        return this;
    }

    public Function<String, String> getUnknownTypeResolver() {
        return unknownTypeResolver;
    }

    /**
     * Set a callback that will be called after avro model was built.
     * Schema model is ready by this point, but you can still modify it by adding custom properties.
     */
    public AvroConfig setAvroSchemaPostProcessor(BiConsumer<AvroSchema, Table> avroSchemaPostProcessor) {
        this.avroSchemaPostProcessor = avroSchemaPostProcessor;
        return this;
    }

    public BiConsumer<AvroSchema, Table> getAvroSchemaPostProcessor() {
        return avroSchemaPostProcessor;
    }

    /**
     * Set to true to use SQL comments at table and field level as optional avro doc fields.
     */
    public AvroConfig setUseSqlCommentsAsDoc(boolean useSqlCommentsAsDoc) {
        this.useSqlCommentsAsDoc = useSqlCommentsAsDoc;
        return this;
    }

    public boolean isUseSqlCommentsAsDoc() {
        return useSqlCommentsAsDoc;
    }

    /**
     * Set the default time logical type to time-micros (default is time-millis)
     */
    public AvroConfig setTimesInMicroseconds(boolean timesInMicroseconds) {
        this.timesInMicroseconds = timesInMicroseconds;
        return this;
    }

    public boolean isTimesInMicroseconds() {
        return timesInMicroseconds;
    }

    public String getDefaultTimeLogicalType() {
        return timesInMicroseconds ? TIME_MICROS : TIME_MILLIS;
    }

    /**
     * Set the default timestamp logical type to timestamp-micros (default is timestamp-millis)
     */
    public AvroConfig setTimestampsInMicroseconds(boolean timestampsInMicroseconds) {
        this.timestampsInMicroseconds = timestampsInMicroseconds;
        return this;
    }

    public boolean isTimestampsInMicroseconds() {
        return timestampsInMicroseconds;
    }

    public String getDefaultTimestampLogicalType() {
        return timestampsInMicroseconds ? TIMESTAMP_MICROS : TIMESTAMP_MILLIS;
    }
}
