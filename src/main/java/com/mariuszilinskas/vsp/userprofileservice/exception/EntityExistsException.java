package com.mariuszilinskas.vsp.userprofileservice.exception;

/**
 * This class represents a custom exception to be thrown when an attempt is made
 * to create an entity that already exists.
 *
 * @author Marius Zilinskas
 */
public class EntityExistsException extends RuntimeException {

    public EntityExistsException(Class<?> entity, String type, Object value) {
        super(entity.getSimpleName() + " with " + type + " '" + value + "' already exists");
    }
}
