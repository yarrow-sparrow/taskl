package com.github.straightth.exception;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.github.straightth.dto.response.ErrorResponse;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = false)
public class ApplicationError extends RuntimeException {

    private static final MustacheFactory MUSTACHE_FACTORY = new DefaultMustacheFactory();
    @Getter
    private final String code;
    @Getter
    private final HttpStatus httpStatus;
    @Getter
    private final String summary;
    @Getter
    private final Level level;
    private final String messageTemplate;
    private final Map<String, Object> parameters;

    public ApplicationError(
            String code,
            HttpStatus httpStatus,
            String summary,
            Level level,
            String messageTemplate,
            Map<String, Object> parameters
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.summary = summary;
        this.level = level;
        this.messageTemplate = messageTemplate;
        this.parameters = parameters;
    }

    public String getMessage() {
        var mustache = MUSTACHE_FACTORY.compile(new StringReader(messageTemplate), messageTemplate);
        var writer = new StringWriter();
        mustache.execute(writer, parameters);
        return writer.toString();
    }

    public ErrorResponse toResponse() {
        return ErrorResponse.builder()
                .code(code)
                .httpStatus(httpStatus.value())
                .summary(summary)
                .message(getMessage())
                .build();
    }

    public enum Level {
        INFO, WARNING, ERROR
    }
}
