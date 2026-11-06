package com.mrudula.poc;

import jakarta.validation.Validator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderQueryParser {

    private final ChatClient chat;
    private final Validator validator;

    public OrderQueryParser(ChatClient.Builder builder, Validator validator) {
        this.chat = builder.build();
        this.validator = validator;
    }

    public ParseResult parse(String question) {
        long start = System.currentTimeMillis();
        try {
            OrderQuery query = chat.prompt()
                    .system("""
                            Convert the user's order question into OrderQuery fields.
                            Today is %s.
                            Use null for any field the user did not mention.
                            Use status ANY if no status is given.
                            Never invent a customer name or an order ID.
                            """.formatted(LocalDate.now()))
                    .user(question)
                    .call()
                    .entity(OrderQuery.class);          // Spring AI: JSON schema from the record + mapping back

            long ms = System.currentTimeMillis() - start;
            if (query == null) {
                return ParseResult.fallback(question, "model returned nothing", ms);
            }

            List<String> errors = validator.validate(query).stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .sorted()
                    .toList();

            return errors.isEmpty()
                    ? ParseResult.ok(question, query, ms)
                    : ParseResult.invalid(question, query, errors, ms);

        } catch (RuntimeException e) {                  // unparseable JSON, or an API error
            return ParseResult.fallback(question, e.getClass().getSimpleName(),
                    System.currentTimeMillis() - start);
        }
    }
}
