package com.github.straightth.config.property;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "taskl.mongodb")
@Value
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class MongoApplicationProperties {

    String image;
}
