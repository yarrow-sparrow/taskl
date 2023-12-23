package com.github.straightth.service.access;

import java.util.Collection;

/**
 * This service implements a fail-fast approach for scenarios where the presence of a queried entity is crucial to ensure data consistency.
 * Example use cases include:
 * - Assigning an entity to a field of another entity.
 * - Updating an entity.
 * - The entityById endpoint.
 * Additionally, this service provides a Secured API that allows querying only the data accessible to the request's author.
 */
public interface AccessService<EntityT, IdT> {

    EntityT getPresentOrThrow(IdT id);

    EntityT getPresentOrThrowSecured(IdT id);

    default void validatePresenceOrThrow(IdT id) {
        getPresentOrThrow(id);
    }

    default void validatePresenceOrThrowSecured(IdT id) {
        getPresentOrThrowSecured(id);
    }

    Collection<EntityT> getPresentOrThrow(Collection<IdT> ids);

    Collection<EntityT> getPresentOrThrowSecured(Collection<IdT> ids);

    default void validatePresenceOrThrow(Collection<IdT> ids) {
        getPresentOrThrow(ids);
    }

    default void validatePresenceOrThrowSecured(Collection<IdT> ids) {
        getPresentOrThrowSecured(ids);
    }
}
