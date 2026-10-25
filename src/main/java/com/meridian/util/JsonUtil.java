package com.meridian.util;

import java.util.Collection;

public final class JsonUtil {

    private JsonUtil() { }

    public static String toJsonArray(Collection<?> items) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : items) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            if (item instanceof Number) {
                sb.append(item);
            } else {
                String text = String.valueOf(item).replace("\\", "\\\\").replace("\"", "\\\"");
                sb.append('"').append(text).append('"');
            }
        }
        return sb.append(']').toString();
    }
}
