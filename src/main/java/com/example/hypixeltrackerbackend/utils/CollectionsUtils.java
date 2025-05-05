package com.example.hypixeltrackerbackend.utils;

import java.util.Collection;

public class CollectionsUtils {
    private CollectionsUtils() {
    }

    /**
     * Null-safe check if the specified collection is not empty.
     * Null returns false.
     *
     * @param collection the collection to check, may be null
     * @return true if non-null and non-empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Null-safe check if the specified collection is empty.
     * Null returns true.
     *
     * @param collection the collection to check, may be null
     * @return true if empty or null
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Get the first and only element in the collection throwing {@code IllegalArgumentException} if there is less or more than one element
     * @param collection the {@link Collection} to get the value from
     * @return the only object
     * @param <E> the type of object in the {@link Collection}
     */
    public static <E> E extractOnlyObjectInCollection(Collection<E> collection){
        if (isEmpty(collection)){
            throw new IllegalArgumentException("Collection is empty or null");
        }
        if (collection.size() != 1) {
            throw new IllegalArgumentException("Collection size is not equal to 1");
        }
        return collection.iterator().next();
    }
}
