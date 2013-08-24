
package net.shibboleth.utilities.java.support.collection;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A specialization of {@link ClassToInstanceMultiMap} which exposes a map-specific
 * instance of {@link ReadWriteLock}. Callers of the map are responsible for explicitly locking
 * (and unlocking) for reading and/or writing, based on application use cases and concurrency 
 * requirements.
 * 
 * @param <B> a bound for the types of values in the map
 */
public class LockableClassToInstanceMultiMap<B> extends ClassToInstanceMultiMap<B> {
    
    /** The map's read write lock. */
    private ReadWriteLock readWriteLock;

    /**
     * Constructor.
     */
    public LockableClassToInstanceMultiMap() {
        super();
        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Constructor.
     *
     * @param isIndexingSupertypes indicates whether supertypes of a value should be indexed
     */
    public LockableClassToInstanceMultiMap(boolean isIndexingSupertypes) {
        super(isIndexingSupertypes);
        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Get the map-specific instance of the {@link ReadWriteLock}.
     * 
     * <p>
     * Callers of the map are responsible for explicitly locking (and unlocking)
     * for reading and/or writing, based on application use cases.
     * </p>
     * 
     * @return Returns the rwlock.
     */
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

}
