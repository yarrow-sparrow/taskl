package com.github.straightth.exception;

import com.google.common.base.Preconditions;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ErrorFactoryInitializer {

    private static final Pattern MUSTACHE_PARAMETER_REGEXP = Pattern.compile("\\{\\{(\\w+)}}");

    public static final ErrorFactory INSTANCE = newInstance();

    private ErrorFactoryInitializer() {
        throw new IllegalStateException("This is a utility class and cannot be instantiated");
    }

    private static ErrorFactory newInstance() {
        var methodToErrorProducer = new HashMap<String, Function<Object[], ApplicationError>>();
        for (var method : ErrorFactory.class.getDeclaredMethods()) {
            if (method.isSynthetic() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            //Validating annotation presence
            var annotation = method.getAnnotation(ErrorTemplate.class);
            if (annotation == null) {
                var message = "Method " + method.getName() + " doesn't annotated with @ErrorTemplate. "
                        + "Every method must be annotated with @ErrorTemplate annotation to implement Error API.";
                throw new IllegalStateException(message);
            }

            //Collecting parameter names
            var parameterNames = Stream.of(method.getParameters())
                    .map(p -> {
                        if (!p.isNamePresent()) {
                            var message = "No parameter name is present. "
                                    + "Program must be compiled with -parameters option.";
                            throw new IllegalStateException(message);
                        }
                        return p.getName();
                    })
                    .collect(Collectors.toList());

            validateTemplate(annotation.message(), parameterNames, method.getName());

            methodToErrorProducer.put(method.getName(), (args) -> handleCall(annotation, parameterNames, args));
        }

        return newProxyInstance(methodToErrorProducer);
    }

    private static void validateTemplate(
            String messageTemplate,
            List<String> parameterNames,
            String methodName
    ) {
        var matcher = MUSTACHE_PARAMETER_REGEXP.matcher(messageTemplate);
        while (matcher.find()) {
            var parameterName = matcher.group(1);
            if (!parameterNames.contains(parameterName)) {
                var message = "Method " + methodName + " has no parameter with name " + parameterName
                        + " in method signature while having it in template.";
                throw new IllegalStateException(message);
            }
        }
    }

    private static ApplicationError handleCall(
            ErrorTemplate annotation,
            List<String> parameterNames,
            Object[] args
    ) {
        Preconditions.checkArgument(
                args == null && parameterNames.isEmpty()
                        || args != null && args.length == parameterNames.size(),
                "Actual method arguments must be similar size to declared parameter names"
        );

        var parameters = new HashMap<String, Object>();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                var parameterName = parameterNames.get(i);
                var arg = args[i];
                parameters.put(parameterName, arg);
            }
        }

        return new ApplicationError(
                annotation.code(),
                annotation.httpStatus(),
                annotation.summary(),
                annotation.level(),
                annotation.message(),
                parameters
        );
    }

    private static ErrorFactory newProxyInstance(
            Map<String, Function<Object[], ApplicationError>> methodToErrorProducer
    ) {
        return (ErrorFactory) Proxy.newProxyInstance(
                ErrorFactory.class.getClassLoader(),
                new Class[] {ErrorFactory.class},
                (proxy, method, args) -> {
                    var methodName = method.getName();
                    var errorProducer = methodToErrorProducer.get(methodName);
                    if (errorProducer == null) {
                        var message = "Error for method " + methodName + "is not found.";
                        throw new RuntimeException(message);
                    }
                    //noinspection SuspiciousInvocationHandlerImplementation
                    return errorProducer.apply(args);
                });
    }
}
