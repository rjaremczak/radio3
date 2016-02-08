package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import com.mindpart.utils.Binary;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

import java.util.HashMap;
import java.util.Map;

import static com.mindpart.radio3.Status.error;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceConnection implements AutoCloseable {
    public static final int TIMEOUT_MS = 200;
    public static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    public static final int DATA_BITS = SerialPort.DATABITS_8;
    public static final int STOP_BITS = SerialPort.STOPBITS_1;
    public static final int PARITY = SerialPort.PARITY_NONE;

    private static Map<Integer, Class<? extends Frame>> frameClasses = new HashMap<>();

    static {
        frameClasses.put(DevicePropertiesResponse.TYPE.getCode(), DevicePropertiesResponse.class);
    }

    private SerialPort serialPort;
    private Status status = Status.OK;

    public DeviceConnection(String portName) {
        this.serialPort = new SerialPort(portName);
    }

    public Status open() {
        try {
            serialPort.openPort();
            this.serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
        } catch (SerialPortException e) {
            status = error(e);
        }
        return status;
    }

    public boolean isConnected() {
        return serialPort!=null && serialPort.isOpened();
    }

    protected void sendWord(int word) throws SerialPortException {
        serialPort.writeByte((byte)Binary.lowByte(word));
        serialPort.writeByte((byte)Binary.highByte(word));
    }

    public Status sendFrame(Frame frame) {
        try {
            sendWord(frame.getType().getCode());
            if(frame.getType().hasPayload()) {
                sendWord(frame.getPayloadSize());
                serialPort.writeBytes(frame.getPayload());
            }
            status = Status.OK;
        } catch (SerialPortException e) {
            status = error(e);
        }
        return status;
    }

    protected int readWord() throws SerialPortException, SerialPortTimeoutException {
        return Binary.word(serialPort.readBytes(2, TIMEOUT_MS));
    }

    public void close() {
        try {
            serialPort.closePort();
            status = Status.OK;
        } catch (SerialPortException e) {
            status = error(e);
        }
    }

    public Status getStatus() {
        return status;
    }

    private Frame createInstance(int typeCode) {
        Class frameClass = frameClasses.get(typeCode);
        if(frameClass!=null) {
            status = Status.OK;
            try {
                return (Frame) frameClass.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                status = error(e.getMessage());
                return null;
            }
        } else {
            status = error(String.format("unknown frame type: %X", typeCode));
            return null;
        }
    }

    public Frame receiveFrame() {
        try {
            Frame frame = createInstance(readWord());
            if(status.isOk()) {
                if(frame.getType().hasPayload()) {
                    int size = readWord();
                    byte[] bytes = serialPort.readBytes(size, TIMEOUT_MS);
                    frame.setPayload(bytes);
                }
                return frame;
            }
        } catch (SerialPortException|SerialPortTimeoutException e) {
            status = error(e.getMessage());
        }
        return null;
    }
}
