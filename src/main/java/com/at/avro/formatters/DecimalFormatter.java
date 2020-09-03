package com.at.avro.formatters;

import com.at.avro.config.FormatterConfig;
import com.at.avro.types.Decimal;

import static java.lang.String.format;

/**
 * @author artur@callfire.com
 */
public class DecimalFormatter implements Formatter<Decimal> {

    @Override
    public String toJson(Decimal decimal, FormatterConfig config) {

        // remove java-class field because its not part of Avro spec
        //String template = "{ \"type\":\"%s\", \"java-class\":\"%s\", \"logicalType\":\"%s\", \"precision\":%s, \"scale\":%s }"
        //    .replaceAll(":", config.colon());

        String template = "{ \"type\":\"%s\", \"logicalType\":\"%s\", \"precision\":%s, \"scale\":%s }"
            .replaceAll(":", config.colon());

        return format(template,
                decimal.getPrimitiveType(),
                decimal.getLogicalType(),
                decimal.getPrecision(),
                decimal.getScale());
    }
}
