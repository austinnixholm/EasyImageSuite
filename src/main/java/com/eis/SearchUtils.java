package com.eis;

import com.eis.models.response.StringSearchResponse;

/**
 * Utility functions for searching.
 *
 * @author Austin Nixholm
 */
public final class SearchUtils {

    /**
     * Finds and returns the first string occurrence between two characters
     * from a source string. The string returned includes the start and end characters.
     *
     * @param first  the first character
     * @param second the second character
     * @param source the source string
     * @return the found occurrence, including the start and end characters.
     */
    public static StringSearchResponse getFirstOccurrenceBetween(char first, char second, String source) {
        StringSearchResponse response = new StringSearchResponse();
        StringBuilder builder = new StringBuilder();
        for (char c : source.toCharArray()) {
            String current = builder.toString();
            // If we've found the first character
            if (c == first) {
                // Ensure that we haven't already found the character as the first
                if (current.startsWith(String.valueOf(first))) continue;
                // If not, start the current string builder
                builder.append(first);
            } else if (c == second && !current.isEmpty() && current.length() != 1) {
                // If we've hit the second character, we already started the builder,
                // and the start char is not the only character within the builder (we have contents),
                // append the final character and end this operation, returning the full found string
                builder.append(c);
                return response.successful(builder.toString());
            }
            // Otherwise, if our current buffer is not empty (we've found the first character already),
            // append what is next.
            else if (!current.isEmpty())
                builder.append(c);
        }
        String result = builder.toString();
        // If there was no result, or the result doesn't end with the last character, return a failed response
        if (result.isEmpty() || !result.endsWith(String.valueOf(second)))
            return response.failed();
        return response.successful(result);
    }

}
