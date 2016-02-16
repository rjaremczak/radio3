package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */

public class DataLink {
    private static final Logger logger = Logger.getLogger(DataLink.class);

    private SerialPort serialPort;

    public static final int TIMEOUT_MS = 200;
    public static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    public static final int DATA_BITS = SerialPort.DATABITS_8;
    public static final int STOP_BITS = SerialPort.STOPBITS_1;
    public static final int PARITY = SerialPort.PARITY_NONE;

    public DataLink(String portName) {
        this.serialPort = new SerialPort(portName);
    }

    public void connect() throws SerialPortException, SerialPortTimeoutException {
        serialPort.openPort();
        this.serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
        int remaining = readAll().length;
        if(remaining>0) {
            logger.debug(remaining+" remaining bytes in read buffer");
        }
    }

    public boolean isConnected() {
        return serialPort!=null && serialPort.isOpened();
    }

    public void disconnect() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            logger.error("exception closing port "+serialPort.getPortName(),e);
        }
    }

    public Frame readFrame() throws SerialPortException, SerialPortTimeoutException {
        FrameHeader header = new FrameHeader(readWord());
        if(header.getSizeBytesCount()>0) {
            header.setSizeBytes(serialPort.readBytes(Math.max(2, header.getSizeBytesCount()), TIMEOUT_MS));
        }
        byte[] payload = serialPort.readBytes(header.getPayloadSize(), TIMEOUT_MS);
        return new Frame(header.getType(), payload);
    }

    public byte[] readAll() throws SerialPortException, SerialPortTimeoutException {
        return serialPort.readBytes(serialPort.getInputBufferBytesCount(), TIMEOUT_MS);
    }

    private void writeWord(int word) throws SerialPortException {
        serialPort.writeByte((byte)Binary.uInt8low(word));
        serialPort.writeByte((byte)Binary.uInt8high(word));
    }

    private int readWord() throws SerialPortException, SerialPortTimeoutException {
        return Binary.uInt16(serialPort.readBytes(2, TIMEOUT_MS));
    }

    public void writeFrame(Frame frame) throws SerialPortException {
        FrameHeader header = new FrameHeader(frame);
        writeWord(header.getHeader());
        if(header.getSizeBytesCount()>0) {
            serialPort.writeBytes(header.getSizeBytes());
        }
    }
}
