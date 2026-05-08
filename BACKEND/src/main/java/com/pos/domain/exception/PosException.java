package com.pos.domain.exception;

public abstract class PosException extends RuntimeException {
    protected PosException(String message) { super(message); }
    protected PosException(String message, Throwable cause) { super(message, cause); }
}
