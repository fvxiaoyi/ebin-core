package core.framework.bson;

import core.framework.bson.codec.BigDecimalCodec;
import core.framework.bson.codec.LocalDateTimeCodec;
import core.framework.bson.codec.ZonedDateTimeCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public final class ExtendCodecRegistry {
    private ExtendCodecRegistry() {
    }

    public static CodecRegistry codecRegistry() {
        List<Codec<?>> codecs = new ArrayList<>(3);
        codecs.add(new LocalDateTimeCodec());
        codecs.add(new ZonedDateTimeCodec());
        codecs.add(new BigDecimalCodec());
        return CodecRegistries.fromCodecs(codecs);
    }
}
