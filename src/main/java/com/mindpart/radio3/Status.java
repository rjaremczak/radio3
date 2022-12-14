package com.mindpart.radio3;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */
public class Status {
    private boolean ok;
    private String message;

    public static final Status OK = new Status(true, "");

    public static Status error(String message) {
        return new Status(false, message);
    }

    public static Status error(Throwable th) {
        return new Status(false, th.getMessage());
    }

    Status(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ok ? "ok" : message;
    }
}
