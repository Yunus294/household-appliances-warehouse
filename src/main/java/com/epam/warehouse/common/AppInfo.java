package com.epam.warehouse.common;

/**
 * What the application tells about itself on startup: name, version, build date and developer.
 *
 * @param contact how to reach the developer: an email or a profile link
 */
public record AppInfo(String name, String version, String date, String developer, String contact) {

    public AppInfo {
        requireText(name, "name");
        requireText(version, "version");
        requireText(date, "date");
        requireText(developer, "developer");
        requireText(contact, "contact");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Application " + field + " must not be empty");
        }
    }
}
