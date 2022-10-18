package core.framework.bson.codec;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author ebin
 */
public class BigDecimalCodec implements Codec<BigDecimal> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigDecimalCodec.class);

    static void write(BsonWriter writer, BigDecimal value) {
        if (value == null) writer.writeNull();
        else writer.writeDouble(value.doubleValue());
    }

    static BigDecimal read(BsonReader reader, String field) {
        BsonType currentType = reader.getCurrentBsonType();
        if (currentType == BsonType.NULL) {
            reader.readNull();
            return null;
        } else if (currentType == BsonType.DOUBLE) {
            return BigDecimal.valueOf(reader.readDouble());
        } else {
            LOGGER.warn("unexpected field type, field={}, type={}", field, currentType);
            reader.skipValue();
            return null;
        }
    }

    @Override
    public void encode(BsonWriter writer, BigDecimal value, EncoderContext context) {
        write(writer, value);
    }

    @Override
    public BigDecimal decode(BsonReader reader, DecoderContext context) {
        return read(reader, reader.getCurrentName());
    }

    @Override
    public Class<BigDecimal> getEncoderClass() {
        return BigDecimal.class;
    }

}