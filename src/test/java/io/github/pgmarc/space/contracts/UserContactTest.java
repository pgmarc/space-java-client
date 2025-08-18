package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class UserContactTest {

    @Test
    void givenIdAndUsernameShouldCreateUserContact() {

        String userId = "123456789";
        String username = "alex";

        UserContact contact = UserContact.builder(userId, username).build();
        assertEquals(userId, contact.getUserId());
        assertEquals(username, contact.getUsername());
    }

    @Test
    void givenNullUserIdShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> UserContact.builder(null, "alex"));
        assertEquals("userId must not be null", ex.getMessage());
    }

    @Test
    void givenNullUsernameShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> UserContact.builder("123456789", null));
        assertEquals("username must not be null", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "ab", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" })
    void givenInvalidUsernamesShouldThrow(String username) {

        assertThrows(IllegalArgumentException.class, () -> UserContact.builder("123456789", username).build());
    }

    // Using pairwise testing
    @ParameterizedTest
    @CsvSource(value = {
            "NIL,NIL,NIL,NIL",
            "Alex,Doe,NIL,666666666",
            "NIL,Doe,alexdoe@example.com,666666666",
            "Alex,NIL,alexdoe@example.com,666666666"
    }, nullValues = "NIL")
    void givenOptionalParametersExpecttoBeDefined(String firstName, String lastName, String email, String phone) {

        UserContact contact = UserContact.builder("123456789", "alexdoe")
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone).build();
        assertEquals(Optional.ofNullable(firstName), contact.getFirstName());
        assertEquals(Optional.ofNullable(lastName), contact.getLastName());
        assertEquals(Optional.ofNullable(email), contact.getEmail());
        assertEquals(Optional.ofNullable(phone), contact.getPhone());
    }

}
