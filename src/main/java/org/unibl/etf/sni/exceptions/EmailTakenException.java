package org.unibl.etf.sni.exceptions;

public class EmailTakenException extends RuntimeException {
    public EmailTakenException() {
        super();
    }

    public EmailTakenException(String message) {
        super(message);
    }
}
