package com.example.COLLABORATION_SERVICE.utils;

public final class CacheKeys {

    private CacheKeys() {
    }

    public static String sentRequests(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        return "sent:"
                + userId
                + ":page:" + page
                + ":size:" + size
                + ":sortBy:" + sortBy
                + ":sortDirection:" + sortDirection;
    }

    public static String receivedRequests(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        return "received:"
                + userId
                + ":page:" + page
                + ":size:" + size
                + ":sortBy:" + sortBy
                + ":sortDirection:" + sortDirection;
    }

    public static String connections(
            Long userId,
            int page,
            int size
    ) {
        return "connections:"
                + userId
                + ":page:" + page
                + ":size:" + size;
    }

    public static String researcherSearch(
            String keyword,
            Long id,
            String firstName,
            String lastName,
            String username,
            String email,
            String phoneNo,
            Object role,
            Object status,
            Boolean emailVerified,
            Boolean accountNonLocked,
            Object createdAfter,
            Object createdBefore,
            int page,
            int size,
            String sortBy
    ) {
        return "search:"
                + safe(keyword)
                + ":" + safe(id)
                + ":" + safe(firstName)
                + ":" + safe(lastName)
                + ":" + safe(username)
                + ":" + safe(email)
                + ":" + safe(phoneNo)
                + ":" + safe(role)
                + ":" + safe(status)
                + ":" + safe(emailVerified)
                + ":" + safe(accountNonLocked)
                + ":" + safe(createdAfter)
                + ":" + safe(createdBefore)
                + ":page:" + page
                + ":size:" + size
                + ":sortBy:" + safe(sortBy);
    }

    public static String researcherProfile(
            Long currentUserId,
            Long researcherId,
            int page,
            int size,
            String sortBy
    ) {
        return "currentUser:"
                + currentUserId
                + ":researcher:"
                + researcherId
                + ":page:" + page
                + ":size:" + size
                + ":sortBy:" + sortBy;
    }

    private static String safe(Object value) {
        return value == null ? "null" : value.toString();
    }
}