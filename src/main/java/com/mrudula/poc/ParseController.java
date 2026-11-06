package com.mrudula.poc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ParseController {

    /** The 5 test questions used in the LinkedIn article. */
    static final List<String> TEST_QUESTIONS = List.of(
            "Show me John Smith's shipped orders from last week",
            "Status of order ORD-48213 for Priya",
            "Cancelled orders for Mike in October",
            "Order 4821 for Sara",
            "What's the weather today?");

    private final OrderQueryParser parser;

    public ParseController(OrderQueryParser parser) {
        this.parser = parser;
    }

    /** GET /parse?q=Show me John's shipped orders from last week */
    @GetMapping("/parse")
    public ParseResult parse(@RequestParam String q) {
        return parser.parse(q);
    }

    /** GET /run-tests : runs all 5 questions in one go (handy for the screenshot). */
    @GetMapping("/run-tests")
    public List<ParseResult> runTests() {
        return TEST_QUESTIONS.stream().map(parser::parse).toList();
    }
}
