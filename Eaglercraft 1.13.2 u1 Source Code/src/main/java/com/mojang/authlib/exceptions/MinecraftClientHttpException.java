package com.mojang.authlib.exceptions;

import com.mojang.authlib.yggdrasil.response.ErrorResponse;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class MinecraftClientHttpException extends MinecraftClientException {

    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    private final int status;
    @Nullable
    private final ErrorResponse response;

    public MinecraftClientHttpException(final int status) {
        super(ErrorType.HTTP_ERROR, getErrorMessage(status, null));
        this.status = status;
        this.response = null;
    }

    public MinecraftClientHttpException(final int status, final ErrorResponse response) {
        super(ErrorType.HTTP_ERROR, getErrorMessage(status, response));
        this.status = status;
        this.response = response;
    }

    public int getStatus() {
        return status;
    }

    public Optional<ErrorResponse> getResponse() {
        return Optional.ofNullable(response);
    }

    @Override
    public String toString() {
        return MinecraftClientHttpException.class.getSimpleName() + "[type=" + type + ", status=" + status + ", response=" + response + "]";
    }

    @Override
    public AuthenticationException toAuthenticationException() {

        //NOTE: Not sure if this is correct/expected mapping for Java client
        //Typically Forbidden (401) means you have not enough privileges to access the service
        if (hasError("InsufficientPrivilegesException") || status == FORBIDDEN) {
            return new InsufficientPrivilegesException(getMessage(), this);
        }

        if (status == UNAUTHORIZED) {
            return new InvalidCredentialsException(getMessage(), this);
        }

        if (status >= 500) {
            return new AuthenticationUnavailableException(getMessage(), this);
        }

        return new AuthenticationException(getMessage(), this);
    }

    private Optional<String> getError() {
        return getResponse()
                   .map(ErrorResponse::getError)
                   .filter(StringUtils::isNotEmpty);
    }

    private static String getErrorMessage(final int status, final ErrorResponse response) {
        final String errorMessage;
        if (response != null) {
            if (StringUtils.isNotEmpty(response.getErrorMessage())) {
                errorMessage = response.getErrorMessage();
            } else if (StringUtils.isNotEmpty(response.getError())) {
                errorMessage = response.getError();
            } else {
                errorMessage = "Status: " + status;
            }
        } else {
            errorMessage = "Status: " + status;
        }
        return errorMessage;
    }

    private boolean hasError(final String error) {
        return getError()
                   .filter(value -> value.equalsIgnoreCase(error))
                   .isPresent();
    }
}
