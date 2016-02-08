package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceService {
    private static Logger logger = Logger.getLogger(DeviceService.class);

    private DeviceConnection connection;

    public boolean isConnected() {
        return connection!=null && connection.isConnected();
    }

    public Status connect(String portName) {
        if(isConnected()) {
            return Status.error("already connected");
        }

        try {
            connection = new DeviceConnection(portName);
        } catch (SerialPortException e) {
            logger.error(e,e);
            return Status.error("connection error");
        }

        return Status.OK;
    }

    public Status disconnect() {
        if(!isConnected()) {
            return Status.error("not connected");
        }

        try {
            connection.close();
        } catch (SerialPortException e) {
            logger.error(e,e);
            return Status.error("disconnection error");
        }

        return Status.OK;
    }

    public DeviceConnection getConnection() {
        return connection;
    }

    public List<String> availableSerialPorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

}
