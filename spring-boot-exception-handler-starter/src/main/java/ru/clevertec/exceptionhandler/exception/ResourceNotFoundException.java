package ru.clevertec.exceptionhandler.exception;

/**
 * An exception that will be thrown if the resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {

    public static final String RESOURCE_WITH_ID_NOT_FOUND = "Resource with id '%s' not found";

    public static final String RESOURCE_WITH_PROPERTY_NOT_FOUND = "Resource with %s '%s' not found";

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException byResourceId(Object resourceId) {
        return new ResourceNotFoundException(
                String.format(RESOURCE_WITH_ID_NOT_FOUND, resourceId)
        );
    }

    public static ResourceNotFoundException byProperty(String property, Object value) {
        return new ResourceNotFoundException(
                String.format(RESOURCE_WITH_PROPERTY_NOT_FOUND, property, value)
        );
    }

}