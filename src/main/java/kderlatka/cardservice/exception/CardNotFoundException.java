package kderlatka.cardservice.exception;

public class UnrecognisedProviderException extends IllegalArgumentException {
    public UnrecognisedProviderException(String message) {
        super(message);
    }
}
