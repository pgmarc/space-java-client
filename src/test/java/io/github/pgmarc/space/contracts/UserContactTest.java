package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
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

    @Test
    void givenRequiredParametersShouldSerializeMinimunJson() {

        UserContact userContact = UserContact.builder(TEST_USER_ID, TEST_USERNAME)
                .build();

        JSONObject userContactJson = userContact.toJson();

        assertAll(
                () -> assertFalse(userContactJson.has("firstName")),
                () -> assertFalse(userContactJson.has("lastName")),
                () -> assertFalse(userContactJson.has("email")),
                () -> assertFalse(userContactJson.has("phone")));
    }

    @Test
    void givenUserContactShouldSerializeToJson() {

        String firstName = "Alex";
        String lastName = "Doe";
        String email = "alex@example.com";
        String phone = "+(34) 123 456 789";

        UserContact userContact = UserContact.builder(TEST_USER_ID, TEST_USERNAME)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();

        JSONObject obj = new JSONObject()
                .put("userId", TEST_USER_ID)
                .put("username", TEST_USERNAME)
                .put("firstName", firstName)
                .put("lastName", lastName)
                .put("email", email)
                .put("phone", phone);

        assertTrue(obj.similar(userContact.toJson()));
    }

    @Test
    void givenUserContactJsonShouldParse() {

        String firtName = "Alex";
        String lastName = "Doe";
        String email = "alexdoe@example.com";
        String phone = "(+34) 666 666 666";
        UserContact expected = UserContact.builder(TEST_USER_ID, TEST_USERNAME)
                .firstName(firtName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();

        JSONObject input = new JSONObject().put("userId", TEST_USER_ID)
                .put("username", TEST_USERNAME)
                .put("firstName", firtName)
                .put("lastName", lastName)
                .put("email", email)
                .put("phone", phone);
        assertEquals(expected, UserContact.fromJson(input));
    }

    @Test
    void givenNullJsonShouldThrow() {

        Exception ex = assertThrows(NullPointerException.class, () -> UserContact.fromJson(null));
        assertEquals("user contact json must not be null", ex.getMessage());
    }

}
