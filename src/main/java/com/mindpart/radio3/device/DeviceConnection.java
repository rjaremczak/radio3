package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import com.mindpart.utils.Binary;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceConnection implements AutoCloseable {
    private static Logger logger = Logger.getLogger(DeviceConnection.class);

    public static final int TIMEOUT_MS = 200;
    public static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    public static final int DATA_BITS = SerialPort.DATABITS_8;
    public static final int STOP_BITS = SerialPort.STOPBITS_1;
    public static final int PARITY = SerialPort.PARITY_NONE;

    private SerialPort serialPort;

    public DeviceConnection(String portName) {
        this.serialPort = new SerialPort(portName);
    }

    public void open() throws SerialPortException {
        serialPort.openPort();
        this.serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
    }

    public boolean isConnected() {
        return serialPort!=null && serialPort.isOpened();
    }

    void sendWord(int word) throws SerialPortException {
        serialPort.writeByte((byte)Binary.uint8low(word));
        serialPort.writeByte((byte)Binary.uint8high(word));
    }

    int readWord() throws SerialPortException, SerialPortTimeoutException {
        return Binary.uint16(serialPort.readBytes(2, TIMEOUT_MS));
    }

    public void sendFrame(Frame frame) throws SerialPortException {
        sendWord(frame.getHeader());
        if(frame.hasPayload()) {
            sendWord(frame.getPayloadSize());
            serialPort.writeBytes(frame.getPayload());
        }
    }

    public void close() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            logger.error("exception closing port "+serialPort.getPortName(),e);
        }
    }

    public Frame receiveFrame() throws SerialPortException, SerialPortTimeoutException {
        Frame frame = new Frame(readWord());
        if(frame.hasPayload()) {
            int size = readWord();
            byte[] bytes = serialPort.readBytes(size, TIMEOUT_MS);
            frame.setPayload(bytes);
        }
        return frame;
    }

    public byte[] readAll() throws SerialPortException, SerialPortTimeoutException {
        return serialPort.readBytes(serialPort.getInputBufferBytesCount(), TIMEOUT_MS);
    }
}
