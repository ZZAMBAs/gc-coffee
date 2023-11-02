package com.example.gccoffee.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void createInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("accc"));
    }

    @Test
    void createValidEmail() {
        String emailAddress = "ac@hanmail.co.kr";
        Email email = new Email(emailAddress);
        assertEquals(email.getAddress(), emailAddress);
    }

    @Test
    void testEmailEquality() {
        Email email1 = new Email("hey@gmail.com");
        Email email2 = new Email("hey@gmail.com");

        assertEquals(email1, email2);
    }
}
