package io.github.pgmarc.space.exceptions;


public class SpaceApiException extends RuntimeException {

    private transient final SpaceApiError error;

    public SpaceApiException(SpaceApiError error) {
        super(error.toString());
        this.error = error;
    }

    public int getCode() {
        return error.getCode();
    }
}
