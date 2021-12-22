package frl.hacklab.hw3.repositories;

public enum RepositoryResult
{
    /**
     * Indicate that an entity has been added or created.
     */
    Created,

    /**
     * Indicate that an entity has been updated.
     */
    Updated,

    /**
     *  Indicate that we didn't succeed into creating an entity.
     */
    CreationNotAllowed,

    /**
     * Indicate that an entity has been deleted.
     */
    Deleted,

    /**
     * Indicate that something could not been found.
     * This is an erroneous state, deal with accordingly.
     */
    NotFound,

    /**
     * Indicate that we tried updating an entity partially. i.e. fields.
     * This is usually caused by user error.
     */
    UpdatePartialNotAllowed,

    /**
     * Indicate that a creation failed because it was a partial entity.
     */
    CreatePartial,

    /**
     * For those that were to lazy to implement all methods.
     */
    NotImplemented,

    /**
     *  To indicate that the operation encountered a null value as input.
     */
    NullValue
}
