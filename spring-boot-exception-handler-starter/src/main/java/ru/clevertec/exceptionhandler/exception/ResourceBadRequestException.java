package ru.clevertec.exceptionhandler.exception;

/**
 * Exception to be thrown if the resource is not valid
 */
public class ResourceBadRequestException extends RuntimeException {

    public static final String RESOURCE_ID_MUST_NOT_BE_NULL = "Resource id must not be null";

    public ResourceBadRequestException(String message) {
        super(message);
    }

    public static ResourceBadRequestException byResourceIdIsNull() {
        return new ResourceBadRequestException(RESOURCE_ID_MUST_NOT_BE_NULL);
    }

}
