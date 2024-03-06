package com.github.yarrow.sparrow.config.mongo.audit;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class EpochMilliToInstantConverter implements Converter<Long, Instant> {

    @Override
    public Instant convert(@NotNull Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli);
    }
}
