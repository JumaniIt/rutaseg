package com.jumani.rutaseg.dto.result;

public class Result<T> {
    private final T response;
    private final Error error;

    public Result(T response) {
        this.response = response;
        this.error = null;
    }

    public Result(Error error) {
        this.response = null;
        this.error = error;
    }

    public final boolean isSuccessful() {
        return response != null;
    }

    public T getResponse() {
        if (this.isSuccessful()) {
            return response;
        } else {
            throw new IllegalStateException("cannot invoke getResponse on an error result");
        }
    }

    public Error getError() {
        if (!this.isSuccessful()) {
            return error;
        } else {
            throw new IllegalStateException("cannot invoke getError on an successful result");
        }
    }
}
