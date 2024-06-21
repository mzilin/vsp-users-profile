package com.mariuszilinskas.vsp.userprofileservice.exception;

public class IncorrectFileException extends RuntimeException {

    public IncorrectFileException(String extension) {
        super("Incorrect file extension: " + extension);
    }

}
