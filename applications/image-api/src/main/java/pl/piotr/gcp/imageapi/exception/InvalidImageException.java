package pl.piotr.gcp.imageapi.exception;

public class InvalidImageException extends RuntimeException {

    public InvalidImageException(String message) {
        super(message);
    }
}