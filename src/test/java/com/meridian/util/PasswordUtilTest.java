package com.meridian.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashIsNotThePlainPassword() {
        assertNotEquals("Secret@123", PasswordUtil.hash("Secret@123"));
    }

    @Test
    void correctPasswordMatchesItsHash() {
        String hash = PasswordUtil.hash("Secret@123");
        assertTrue(PasswordUtil.matches("Secret@123", hash));
    }

    @Test
    void wrongPasswordDoesNotMatch() {
        String hash = PasswordUtil.hash("Secret@123");
        assertFalse(PasswordUtil.matches("Other@123", hash));
    }

    @Test
    void malformedHashDoesNotMatch() {
        assertFalse(PasswordUtil.matches("Secret@123", "not-a-hash"));
    }
}
