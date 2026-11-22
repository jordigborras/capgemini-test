package com.capgemini.test.code.exceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException() {
        super("Room not found");
    }
}