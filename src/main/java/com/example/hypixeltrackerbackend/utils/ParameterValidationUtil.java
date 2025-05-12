package com.example.hypixeltrackerbackend.utils;

public class ParameterValidationUtil {
    private static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    private static final String ITEM_ID_REGEX = "^[A-Z]+(_[A-Z]+)*+$";

    private ParameterValidationUtil() {}

    public static boolean isValidProfileId(String profileId) {
        return isValidUUID(profileId);
    }

    public static boolean isValidMinecraftId(String minecraftId) {
        return isValidUUID(minecraftId);
    }

    public static boolean isValidItemID(String id) {
        return id.matches(ITEM_ID_REGEX);
    }

    private static boolean isValidUUID(String uuid) {
        return uuid.matches(UUID_REGEX);
    }
}
