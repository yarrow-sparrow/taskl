package com.github.straightth.service.access;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractAccessService<EntityT, IdT, NotFoundExceptionT extends IllegalArgumentException>
        implements AccessService<EntityT, IdT> {

    public abstract Function<Collection<IdT>, Collection<EntityT>> defaultAccessFunction();
    public abstract Function<Collection<IdT>, Collection<EntityT>> securedAccessFunction();
    public abstract Supplier<NotFoundExceptionT> notFoundExceptionSupplier();

    private Collection<EntityT> getPresentOrThrowInternal(
            Collection<IdT> ids,
            Function<Collection<IdT>, Collection<EntityT>> accessFunction
    ) {
        //ensure that id is unique
        ids = new HashSet<>(ids);
        //finding entities
        var presentEntities = accessFunction.apply(ids);
        //checking if number of entities is equal to queried number of ids
        if (presentEntities.size() != ids.size()) {
            throw notFoundExceptionSupplier().get();
        }
        return presentEntities;
    }

    private EntityT getPresentOrThrowInternal(
            IdT id,
            Function<Collection<IdT>, Collection<EntityT>> accessFunction
    ) {
        return getPresentOrThrowInternal(List.of(id), accessFunction).stream()
                .findFirst()
                .orElseThrow(notFoundExceptionSupplier());
    }


    public EntityT getPresentOrThrow(IdT id) {
        return getPresentOrThrowInternal(id, defaultAccessFunction());
    }

    public EntityT getPresentOrThrowSecured(IdT id) {
        return getPresentOrThrowInternal(id, securedAccessFunction());
    }

    public Collection<EntityT> getPresentOrThrow(Collection<IdT> ids) {
        return getPresentOrThrowInternal(ids, defaultAccessFunction());
    }

    public Collection<EntityT> getPresentOrThrowSecured(Collection<IdT> ids) {
        return getPresentOrThrowInternal(ids, securedAccessFunction());
    }
}
