package com.meridian.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidationUtilTest {

    @Test
    void blankValuesAreDetected() {
        assertTrue(ValidationUtil.isBlank(null));
        assertTrue(ValidationUtil.isBlank("   "));
        assertFalse(ValidationUtil.isBlank("meridian"));
    }

    @Test
    void validEmailsAreAccepted() {
        assertTrue(ValidationUtil.isValidEmail("student@galgotias.edu"));
        assertTrue(ValidationUtil.isValidEmail("  admin@meridian.com "));
    }

    @Test
    void invalidEmailsAreRejected() {
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("missing@domain"));
        assertFalse(ValidationUtil.isValidEmail("@nouser.com"));
    }

    @Test
    void passwordNeedsLengthLetterAndDigit() {
        assertTrue(ValidationUtil.isStrongPassword("Admin@123"));
        assertFalse(ValidationUtil.isStrongPassword("short1"));
        assertFalse(ValidationUtil.isStrongPassword("onlyletters"));
        assertFalse(ValidationUtil.isStrongPassword("12345678"));
        assertFalse(ValidationUtil.isStrongPassword(null));
    }

    @Test
    void parseIdReturnsMinusOneForBadInput() {
        assertEquals(15, ValidationUtil.parseId(" 15 "));
        assertEquals(-1, ValidationUtil.parseId("abc"));
        assertEquals(-1, ValidationUtil.parseId(null));
    }
}
