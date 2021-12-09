package frl.hacklab.hw3.repositories;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/*
    CRUD+E interface.
    C -> Create
    R -> Read
    U -> Update
    D -> Delete
*/
public interface CrudInterface<T>
{
    RepositoryResult create(T toCreate);
    Optional<T> read(T toRead);
    RepositoryResult update(T toUpdate, boolean onlyAllowFullUpdates);
    RepositoryResult delete(T toDelete);
    HashSet<T> everything();
}
