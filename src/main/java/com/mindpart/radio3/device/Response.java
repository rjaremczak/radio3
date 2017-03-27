package com.mindpart.radio3.device;

import static com.mindpart.radio3.device.Response.Status.EXCEPTION;
import static com.mindpart.radio3.device.Response.Status.OK;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.27
 */
public class Response {
    public enum Status {
        OK, UNEXPECTED_FRAME, INVALID_FRAME, ERROR, EXCEPTION, TIMEOUT;

        public String getName() {
            return name().replace('_',' ').toLowerCase();
        }
    }

    private String message;
    private Frame frame;
    private Throwable throwable;
    private Status status;

    public Response(Frame frame) {
        this.frame = frame;
        this.status = OK;
    }

    public Response(Throwable throwable) {
        this.throwable = throwable;
        this.status = EXCEPTION;
    }

    public Response(Status status, String message) {
        if(status == OK || status == EXCEPTION) throw new IllegalArgumentException("invalid status value");

        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Frame getFrame() {
        return frame;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Status getStatus() {
        return status;
    }
}
