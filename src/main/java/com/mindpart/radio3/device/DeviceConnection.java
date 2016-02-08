package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceConnection implements AutoCloseable {
    public static final int TIMEOUT_MS = 1000;
    public static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    public static final int DATA_BITS = SerialPort.DATABITS_8;
    public static final int STOP_BITS = SerialPort.STOPBITS_1;
    public static final int PARITY = SerialPort.PARITY_NONE;

    private SerialPort serialPort;

    public DeviceConnection(String portName) throws SerialPortException {
        this.serialPort = new SerialPort(portName);
        this.serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
    }

    public void open() throws SerialPortException {
        serialPort.openPort();
    }

    public boolean isConnected() {
        return serialPort!=null && serialPort.isOpened();
    }

    public void sendWord(int word) throws SerialPortException {
        serialPort.writeByte((byte)Binary.lowByte(word));
        serialPort.writeByte((byte)Binary.highByte(word));
    }

    public void sendFrame(Frame frame) throws SerialPortException {
        sendWord(frame.getSize());
        sendWord(frame.getType());
        serialPort.writeBytes(frame.getPayload());
    }

    public void sendText(String str) throws UnsupportedEncodingException, SerialPortException {
        sendWord(str.length());
        serialPort.writeString(str, StandardCharsets.US_ASCII.name());
    }

    public int receiveWord() throws SerialPortException, SerialPortTimeoutException {
        return Binary.word(serialPort.readBytes(2, TIMEOUT_MS));
    }

    public String receiveText() throws SerialPortException, SerialPortTimeoutException {
        return serialPort.readString(receiveWord(), TIMEOUT_MS);
    }

    public void close() throws SerialPortException {
        serialPort.closePort();
    }
}
