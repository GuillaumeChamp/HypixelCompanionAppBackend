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
}
