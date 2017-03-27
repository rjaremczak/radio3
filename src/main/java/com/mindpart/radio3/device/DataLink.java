package com.mindpart.radio3.device;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */

public interface DataLink {
    void connect(String portName) throws Exception;
    void disconnect();
    void writeFrame(Frame frame) throws Exception;
    String getPortName();
    boolean isOpened();
    Frame request(Frame request);
    List<String> availablePorts();
}
