package kderlatka.cardservice.exception;

import java.util.NoSuchElementException;

public class CardNotFoundException extends NoSuchElementException {
    public CardNotFoundException(String message) {
        super(message);
    }
}
