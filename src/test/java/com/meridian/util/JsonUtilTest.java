package com.meridian.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

    @Test
    void textValuesAreQuoted() {
        assertEquals("[\"Mon\",\"Tue\"]", JsonUtil.toJsonArray(List.of("Mon", "Tue")));
    }

    @Test
    void numbersAreNotQuoted() {
        assertEquals("[1,2.5,0]", JsonUtil.toJsonArray(List.of(1, 2.5, 0)));
    }

    @Test
    void quotesInsideTextAreEscaped() {
        assertEquals("[\"say \\\"hi\\\"\"]", JsonUtil.toJsonArray(List.of("say \"hi\"")));
    }

    @Test
    void emptyCollectionGivesEmptyArray() {
        assertEquals("[]", JsonUtil.toJsonArray(List.of()));
    }
}
