package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import static com.mindpart.radio3.Status.OK;
import static com.mindpart.radio3.Status.error;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceService {
    private static final int MAX_ATTEMPTS = 3;
    private static final Logger logger = Logger.getLogger(DeviceService.class);

    private DeviceConnection connection;
    private Status status = OK;

    private DeviceInfoParser deviceInfoParser = new DeviceInfoParser();

    public boolean isConnected() {
        return connection!=null && connection.isConnected();
    }

    public Status connect(String portName) {
        logger.debug("connect to "+portName);
        if(isConnected()) {
            status = error("already connected");
        } else {
            connection = new DeviceConnection(portName);
            try {
                connection.open();
                status = OK;
            } catch (SerialPortException e) {
                status = error(e);
            }
        }

        logger.debug(status);
        return status;
    }

    public DeviceInfo readDeviceInfo() {
        if(!isConnected()) {
            status = error("not connected");
        } else {
            for(int i=0; i<MAX_ATTEMPTS; i++) {
                DeviceInfo deviceInfo = (DeviceInfo)performRequest(DeviceInfoParser.REQUEST, deviceInfoParser);
                if(status == OK) {
                    return deviceInfo;
                }
                logger.warn("attempt "+(i+1)+"/"+MAX_ATTEMPTS+" failed: "+status);
            }
        }
        return null;
    }

    Object performRequest(Frame request, FrameParser frameParser) {
        try {
            connection.sendFrame(request);
            Frame response = connection.receiveFrame();
            if(frameParser.recognizes(response)) {
                status = OK;
                return frameParser.parse(response);
            } else {
                int remaining = connection.readAll().length;
                status = error("unexpected frame: "+response+" (remaining bytes: "+remaining+")");
            }
        } catch (Exception e) {
            status = error(e);
        }
        return null;
    }

    public Status disconnect() {
        logger.debug("disconnect");
        if(!isConnected()) {
            status = error("not connected");
        } else {
            connection.close();
            connection = null;
        }
        logger.debug(status);
        return status;
    }

    public List<String> availableSerialPorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    public Status getStatus() {
        return status;
    }
}
