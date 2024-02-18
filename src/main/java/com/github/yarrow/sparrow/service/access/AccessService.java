package com.github.yarrow.sparrow.service.access;

import java.util.Collection;

/**
 * This service implements a fail-fast approach
 *  for scenarios where the presence of a queried entity is crucial to ensure data consistency.
 * Example use cases include:
 * - Assigning an entity to a field of another entity.
 * - Updating an entity.
 * - The entityById endpoint.
 * This service also provides a Secured API that allows querying only the data accessible to the request's author.
 */
@SuppressWarnings("unused")
public interface AccessService<EntityT, IdT> {

    EntityT getPresentOrThrow(IdT id);

    Collection<EntityT> getPresentOrThrow(Collection<IdT> ids);

    EntityT getPresentOrThrowSecured(IdT id);

    Collection<EntityT> getPresentOrThrowSecured(Collection<IdT> ids);

    default void validatePresenceOrThrow(IdT id) {
        getPresentOrThrow(id);
    }

    default void validatePresenceOrThrow(Collection<IdT> ids) {
        getPresentOrThrow(ids);
    }

    default void validatePresenceOrThrowSecured(IdT id) {
        getPresentOrThrowSecured(id);
    }

    default void validatePresenceOrThrowSecured(Collection<IdT> ids) {
        getPresentOrThrowSecured(ids);
    }
}
