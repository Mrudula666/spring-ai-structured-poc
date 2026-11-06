package com.mrudula.poc;

import java.util.List;

/** What the API returns: OK, INVALID (caught by Bean Validation) or FALLBACK (model output unusable). */
public record ParseResult(
        String question,
        String result,
        OrderQuery query,
        List<String> errors,
        String message,
        long latencyMs) {

    static ParseResult ok(String q, OrderQuery query, long ms) {
        return new ParseResult(q, "OK", query, List.of(), null, ms);
    }

    static ParseResult invalid(String q, OrderQuery query, List<String> errors, long ms) {
        return new ParseResult(q, "INVALID", query, errors, null, ms);
    }

    static ParseResult fallback(String q, String reason, long ms) {
        return new ParseResult(q, "FALLBACK", null, List.of(),
                "Sorry, I couldn't understand that. Try: \"orders for <name> shipped last week\". (" + reason + ")", ms);
    }
}
