package com.mojang.authlib.yggdrasil.response;

public class Response {
    private String error;
    private String errorMessage;
    private String cause;

    public String getError() {
        return error;
    }

    public String getCause() {
        return cause;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    protected void setError(final String error) {
        this.error = error;
    }

    protected void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    protected void setCause(final String cause) {
        this.cause = cause;
    }
}
