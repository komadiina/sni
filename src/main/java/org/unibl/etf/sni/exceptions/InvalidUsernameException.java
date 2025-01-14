package org.unibl.etf.sni.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
        super();
    }

    public InvalidUsernameException(String message) {
        super(message);
    }
}
