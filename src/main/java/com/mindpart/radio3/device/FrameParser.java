package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.13
 */
public interface FrameParser<T> {
    boolean recognizes(Frame frame);
    T parse(Frame frame);
}
