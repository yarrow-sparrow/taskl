package com.github.yarrow.sparrow.config.property;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "taskl.jwt")
@Value
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class JwtProperties {

    @NotNull
    String signingKey;

    @NotNull
    public String getSigningKey() {
        return Objects.requireNonNull(signingKey, "Signing key is null, auth is not enough safe to continue");
    }
}
