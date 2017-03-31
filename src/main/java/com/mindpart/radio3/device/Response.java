package com.mindpart.radio3.device;

import static com.mindpart.radio3.device.Response.Status.EXCEPTION;
import static com.mindpart.radio3.device.Response.Status.OK;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.27
 */
public class Response<T> {
    public enum Status {
        OK, UNEXPECTED_FRAME, INVALID_FRAME, ERROR, EXCEPTION, TIMEOUT;

        public String getName() {
            return name().replace('_',' ').toLowerCase();
        }
    }

    private Status status;
    private T data;
    private Throwable throwable;
    private String message;

    private Response(Status status, T data, Throwable throwable, String message) {
        this.status = status;
        this.data = data;
        this.throwable = throwable;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isOK() {
        return status == OK;
    }

    public static <T> Response<T> success(T data) {
        return new Response(OK, data, null, null);
    }

    public static <T,U> Response<T> error(Status status, String message) {
        if(status == OK) throw new IllegalArgumentException("OK status not alowed");


        return new Response<T>(status, null, null, message);
    }

    public static <T,U> Response<T> error(Response<U> response) {
        return new Response<T>(response.getStatus(), null, response.getThrowable(), response.getMessage());
    }

    public static <T,U> Response<T> error(Throwable throwable) {
        return new Response<T>(EXCEPTION, null, throwable, null);
    }
}
