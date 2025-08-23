package io.github.pgmarc.space.exceptions;

import java.util.Collections;
import java.util.Set;

public class SpaceApiError {

    private final Set<String> messages;

    private final int code;

    public SpaceApiError(int code, Set<String> messages) {
        this.code = code;
        this.messages = Collections.unmodifiableSet(messages);
    }

    int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.join("\n", messages);
    }

}
