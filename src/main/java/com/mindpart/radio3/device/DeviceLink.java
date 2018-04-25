package com.mindpart.radio3.device;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */

public interface DeviceLink {
    void connect(String portName) throws Exception;
    void disconnect();
    String getPortName();
    boolean isOpened();
    Response request(Frame request);
    List<String> availablePorts();
}
