package com.github.foxy.client;

public class LoadException extends RuntimeException {
    public LoadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
