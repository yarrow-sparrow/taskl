package com.github.straightth.service.access;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StringAccessServiceMock extends AbstractAccessService<String, String, StringNotFoundException> {

    private final Set<String> notSecuredString = new HashSet<>();
    private final Set<String> securedString = new HashSet<>();

    @Override
    public Function<Collection<String>, Collection<String>> defaultAccessFunction() {
        return s -> s.stream()
                .filter(notSecuredString::contains)
                .collect(Collectors.toList());
    }

    @Override
    public Function<Collection<String>, Collection<String>> securedAccessFunction() {
        return s -> s.stream()
                .filter(securedString::contains)
                .toList();
    }

    @Override
    public Supplier<StringNotFoundException> notFoundExceptionSupplier() {
        return StringNotFoundException::new;
    }

    public void add(String string) {
        notSecuredString.add(string);
    }

    public void addSecured(String string) {
        securedString.add(string);
    }
}
