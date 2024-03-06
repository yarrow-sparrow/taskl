package com.github.yarrow.sparrow.config.mongo.audit;

import java.time.Instant;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class InstantToEpochMilliConverter implements Converter<Instant, Long> {

    @Override
    public Long convert(Instant instant) {
        return instant.toEpochMilli();
    }
}