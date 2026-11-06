package com.mrudula.poc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * The contract the model must fill. If it can't fill it correctly,
 * the request never reaches the (imaginary) order database.
 */
public record OrderQuery(
        @NotBlank String customerName,
        @Pattern(regexp = "ORD-\\d{5}") String orderId,   // optional, but must match if present
        @NotNull OrderStatus status,
        LocalDate fromDate,
        LocalDate toDate) {
}
