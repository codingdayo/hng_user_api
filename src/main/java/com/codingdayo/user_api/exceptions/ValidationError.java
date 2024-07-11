package com.codingdayo.user_api.exceptions;

public record ValidationError(
        String field,
        String message
) {
}
