package kderlatka.cardservice.providerintegration.provider;

class InvalidCardProviderException extends IllegalArgumentException {
    InvalidCardProviderException(String message) {
        super(message);
    }
}
