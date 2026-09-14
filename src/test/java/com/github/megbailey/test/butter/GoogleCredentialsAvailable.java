package com.github.megbailey.test.butter;

final class GoogleCredentialsAvailable {
    private GoogleCredentialsAvailable() {
    }

    static boolean isPresent() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = GoogleCredentialsAvailable.class.getClassLoader();
        }
        return classLoader.getResource("client_secret.json") != null;
    }
}
