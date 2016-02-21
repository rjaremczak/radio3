package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;
import com.mindpart.utils.Crc8;
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

    private static final int TIMEOUT_MS = 200;
    private static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    private static final int DATA_BITS = SerialPort.DATABITS_8;
    private static final int STOP_BITS = SerialPort.STOPBITS_1;
    private static final int PARITY = SerialPort.PARITY_NONE;

    public DataLink(String portName) {
        this.serialPort = new SerialPort(portName);
    }

    public void connect() throws SerialPortException, SerialPortTimeoutException {
        serialPort.openPort();
        serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY, false, false);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        flushReadBuffer();
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

    public Frame readFrame() throws SerialPortException, SerialPortTimeoutException, Crc8.Error {
        Crc8 crc8 = new Crc8();
        byte[] headerBytes = serialPort.readBytes(2, TIMEOUT_MS);
        crc8.addBuf(headerBytes);
        FrameHeader header = new FrameHeader(Binary.toUInt16(headerBytes));
        if(header.getSizeBytesCount()>0) {
            byte[] sizeBytes = serialPort.readBytes(Math.max(2, header.getSizeBytesCount()), TIMEOUT_MS);
            header.setSizeBytes(sizeBytes);
            crc8.addBuf(sizeBytes);
        }
        byte[] payload = serialPort.readBytes(header.getPayloadSize(), TIMEOUT_MS);
        crc8.addBuf(payload);
        int receivedCrc = serialPort.readBytes(1, TIMEOUT_MS)[0] & 0xff;
        if(receivedCrc != crc8.getCrc()) {
            throw new Crc8.Error(receivedCrc, crc8.getCrc());
        }
        return new Frame(header.getType(), payload);
    }

    public int flushReadBuffer() {
        try {
            int remaining = serialPort.getInputBufferBytesCount();
            if(remaining>0) {
                serialPort.readBytes(remaining, TIMEOUT_MS);
                logger.debug("flushed "+remaining+" bytes from read buffer");
            }
            return remaining;
        } catch (Exception e) {
            logger.error("error flushing read buffer", e);
        }
        return 0;
    }

    private void writeWord(int word) throws SerialPortException {
        serialPort.writeByte((byte)Binary.toUInt8low(word));
        serialPort.writeByte((byte)Binary.toUInt8high(word));
    }

    private int readWord() throws SerialPortException, SerialPortTimeoutException {
        return Binary.toUInt16(serialPort.readBytes(2, TIMEOUT_MS));
    }

    public void writeFrame(Frame frame) throws SerialPortException {
        serialPort.purgePort(SerialPort.PURGE_TXCLEAR|SerialPort.PURGE_RXCLEAR);
        FrameHeader header = new FrameHeader(frame);
        Crc8 crc8 = new Crc8();
        writeWord(header.getHeader());
        crc8.addWord(header.getHeader());
        if(header.getSizeBytesCount()>0) {
            serialPort.writeBytes(header.getSizeBytes());
            crc8.addBuf(header.getSizeBytes());
        }
        if(frame.getPayloadSize()>0) {
            serialPort.writeBytes(frame.getPayload());
            crc8.addBuf(frame.getPayload());
        }
        serialPort.writeByte((byte)(crc8.getCrc() & 0xff));
    }
}
