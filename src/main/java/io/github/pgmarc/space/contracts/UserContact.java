package io.github.pgmarc.space.contracts;

import java.util.Objects;
import java.util.Optional;

public final class UserContact {

    private final String userId;
    private final String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    private UserContact(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
    }

    public static Builder builder(String userId, String username) {
        return new Builder(Objects.requireNonNull(userId, "userId must not be null"),
                validateUsername(Objects.requireNonNull(username, "username must not be null")));
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

    private static String validateUsername(String username) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("username is blank");
        }

        int length = username.length();
        if (length < 3 || length > 30) {
            throw new IllegalArgumentException("username must be between 3 and 30 characters. Current " + username
                    + " has " + length + "characters");
        }

        return username;
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
            return new UserContact(this);
        }
    }

}
