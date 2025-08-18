package io.github.pgmarc.space.contracts;

import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;

import io.github.pgmarc.space.Jsonable;

public final class UserContact implements Jsonable {

    private final String userId;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;

    private UserContact(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
    }

    public static Builder builder(String userId, String username) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(username, "username must not be null");
        return new Builder(userId, username);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getPhone() {
        return Optional.ofNullable(phone);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserContact other = (UserContact) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public static class Builder {

        private final String userId;
        private final String username;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        private Builder(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserContact build() {
            if (username.isBlank()) {
                throw new IllegalArgumentException("username is blank");
            }
            validateUserNameLength();
            return new UserContact(this);
        }

        private void validateUserNameLength() {
            int length = this.username.length();
            boolean isValidLength = length >= 3 && length <= 30;
            if (!isValidLength) {
                throw new IllegalArgumentException("username must be between 3 and 30 characters. Current " + username
                        + " has " + length + "characters");
            }
        }
    }

    private enum JsonKeys {

        USER_ID("userId"),
        USERNAME("username"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        EMAIL("email"),
        PHONE("phone");

        private final String name;

        private JsonKeys(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject()
                .put(JsonKeys.USER_ID.toString(), userId)
                .put(JsonKeys.USERNAME.toString(), username)
                .putOpt(JsonKeys.FIRST_NAME.toString(), firstName)
                .putOpt(JsonKeys.LAST_NAME.toString(), lastName)
                .putOpt(JsonKeys.EMAIL.toString(), email)
                .putOpt(JsonKeys.PHONE.toString(), phone);

        return obj;
    }

}
