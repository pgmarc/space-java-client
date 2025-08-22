package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class UserContactTest {

    private static final String TEST_USER_ID = "123456789";
    private static final String TEST_USERNAME = "alex";

    @Test
    void givenIdAndUsernameShouldCreateUserContact() {

        UserContact contact = UserContact.builder(TEST_USER_ID, TEST_USERNAME).build();
        assertEquals(TEST_USER_ID, contact.getUserId());
        assertEquals(TEST_USERNAME, contact.getUsername());
    }

    @Test
    void givenNullUserIdShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> UserContact.builder(null, TEST_USERNAME));
        assertEquals("userId must not be null", ex.getMessage());
    }

    @Test
    void givenNullUsernameShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class,
                () -> UserContact.builder(TEST_USER_ID, null));
        assertEquals("username must not be null", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "ab", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" })
    void givenInvalidUsernamesShouldThrow(String username) {

        assertThrows(IllegalArgumentException.class, () -> UserContact.builder(TEST_USER_ID, username).build());
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

        UserContact contact = UserContact.builder(TEST_USER_ID, TEST_USERNAME)
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
