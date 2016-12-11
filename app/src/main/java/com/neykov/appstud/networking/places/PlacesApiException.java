package com.neykov.appstud.networking.places;

/**
 * Created by Georgi on 12/10/2016.
 */

public class PlacesApiException extends Exception{
    private Status status;

    public PlacesApiException(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
