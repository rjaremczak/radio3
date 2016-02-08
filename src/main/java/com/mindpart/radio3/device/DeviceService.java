package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import com.mindpart.utils.Binary;
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
    private static Logger logger = Logger.getLogger(DeviceService.class);

    private DeviceConnection connection;
    private Status status = Status.OK;

    public boolean isConnected() {
        return connection!=null && connection.isConnected();
    }

    public Status connect(String portName) {
        logger.debug("connect to "+portName);
        if(isConnected()) {
            status = error("already connected");
        } else {
            connection = new DeviceConnection(portName);
            status = connection.open();
        }

        logger.debug(status);
        return status;
    }

    public DevicePropertiesResponse readProperties() {
        if(!isConnected()) {
            status = error("not connected");
        } else {
            try {
                connection.sendWord(0x1234);
                int major = connection.readWord();
                int minor = connection.readWord();

                byte[] bytes = new byte[4];
                bytes[0] = (byte)Binary.lowByte(major);
                bytes[1] = (byte)Binary.highByte(major);
                bytes[2] = (byte)Binary.lowByte(minor);
                bytes[3] = (byte)Binary.highByte(minor);
                DevicePropertiesResponse response = new DevicePropertiesResponse();
                response.setPayload(bytes);
                status = OK;
                return response;

            } catch (Exception e) {
                status = error(e);
            }
            /*
            if(connection.sendFrame(DeviceStatusRequest.FRAME).isOk()) {
                Frame response = connection.receiveFrame();
                if(connection.getStatus().isOk()) {
                    status = Status.OK;
                    return (DevicePropertiesResponse) response;
                } else {
                    status = connection.getStatus();
                }
            } else {
                status = connection.getStatus();
            }
            */
        }
        return null;
    }

    public Status disconnect() {
        logger.debug("disconnect");
        if(!isConnected()) {
            status = error("not connected");
        } else {
            connection.close();
            status = connection.getStatus();
            connection = null;
        }
        logger.debug(status);
        return status;
    }

    public DeviceConnection getConnection() {
        return connection;
    }

    public List<String> availableSerialPorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    public Status getStatus() {
        return status;
    }
}
