package io.geewit.utils.javafx.control.paged;

@FunctionalInterface
public interface MessageProvider {

    String message(String key, Object... args);

    static MessageProvider identity() {
        return (k, _) -> k; // fallback
    }
}