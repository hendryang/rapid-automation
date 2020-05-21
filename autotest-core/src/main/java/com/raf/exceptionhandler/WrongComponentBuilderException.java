package com.raf.exceptionhandler;

public class WrongComponentBuilderException extends RuntimeException {
    public WrongComponentBuilderException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
