package com.example.hypixeltrackerbackend.web.responses;

/**
 * Record to describe the response to send while asking to resolve a player uuid from username.
 * @see com.example.hypixeltrackerbackend.web.RequestController#getPlayerUUID(String)
 * @param uuid the uuid
 * @param name the username
 * @param urlPath the urlPath of the avatar image
 */
public record UUIDResponse(String uuid, String name, String urlPath) {
}
