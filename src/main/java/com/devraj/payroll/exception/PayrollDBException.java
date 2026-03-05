package com.devraj.payroll.exception;

/**
 * [Devraj]: Custom exception for payroll database operations
 * Wraps SQLExceptions with a meaningful message to avoid leaking SQL internals.
 * Extends RuntimeException so callers are not forced to catch it.
 */
public class PayrollDBException extends RuntimeException {

    // [Devraj]: Constructor wraps the original SQL cause with a descriptive message
    public PayrollDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
