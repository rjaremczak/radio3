package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;
import com.mindpart.utils.Crc8;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */

public class DataLink {
    private static final Logger logger = Logger.getLogger(DataLink.class);

    private static final int TIMEOUT_MS = 2000;
    private static final int DATA_BITS = SerialPort.DATABITS_8;
    private static final int STOP_BITS = SerialPort.STOPBITS_1;
    private static final int PARITY = SerialPort.PARITY_NONE;

    private SerialPort serialPort;
    private Consumer<Frame> frameHandler;
    private int portBaudRate;

    private volatile AtomicReference<Frame> receivedFrameRef = new AtomicReference<>();
    private volatile CountDownLatch receivedFrameLatch;
    private volatile boolean newHandling = false;

    public DataLink(String portName, int portBaudRate, Consumer<Frame> frameHandler) {
        this.serialPort = new SerialPort(portName);
        this.portBaudRate = portBaudRate;
        this.frameHandler = frameHandler;
    }

    public synchronized void connect() throws SerialPortException, SerialPortTimeoutException {
        if(serialPort.openPort()) {
            logger.debug("port opened");
            serialPort.setParams(portBaudRate, DATA_BITS, STOP_BITS, PARITY, false, false);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            flushReadBuffer();
            attachEventListener();
        } else {
            logger.error("port not opened");
        }
    }

    private void attachEventListener() throws SerialPortException {
        serialPort.addEventListener(serialPortEvent -> {
            try {
                Frame frame = readFrame();
                flushReadBuffer();
                if(newHandling) {
                    receivedFrameRef.set(frame);
                } else {
                    frameHandler.accept(frame);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                if(newHandling) {
                    receivedFrameLatch.countDown();
                }
            }
        }, SerialPort.MASK_RXCHAR);
    }

    public synchronized void disconnect() {
        try {
            flushReadBuffer();
            serialPort.removeEventListener();
            serialPort.closePort();
            logger.debug("port closed");
        } catch (SerialPortException e) {
            logger.error("exception closing port "+serialPort.getPortName(),e);
        }
    }

    private byte[] readBytes(int num) throws SerialPortException, SerialPortTimeoutException {
        ByteBuffer buf = ByteBuffer.allocate(num);
        int remaining = num;
        while(remaining > 0) {
            int size = Math.min(remaining, serialPort.getInputBufferBytesCount());
            byte[] received = serialPort.readBytes(size, TIMEOUT_MS);
            buf.put(received);
            remaining -= received.length;
        }
        return buf.array();
    }

    private Frame readFrame() throws SerialPortException, SerialPortTimeoutException, Crc8.Error {
        Crc8 crc8 = new Crc8();
        byte[] headerBytes = readBytes(2);
        crc8.addBytes(headerBytes);
        FrameHeader header = FrameHeader.fromCode(Binary.toUInt16(headerBytes));
        if(header.getSizeBytesCount()>0) {
            byte[] sizeBytes = readBytes(Math.max(2, header.getSizeBytesCount()));
            header.setSizeBytes(sizeBytes);
            crc8.addBytes(sizeBytes);
        }
        byte[] payload = readBytes(header.getPayloadSize());
        crc8.addBytes(payload);
        int receivedCrc = readBytes(1)[0] & 0xff;
        if(receivedCrc != crc8.getCrc()) {
            throw new Crc8.Error(receivedCrc, crc8.getCrc());
        }
        if(serialPort.getInputBufferBytesCount()!=0) {
            logger.warn("remaining "+serialPort.getInputBufferBytesCount()+" bytes in RX buffer");
        }

        return new Frame(header.getCommand(), payload);
    }

    private int flushReadBuffer() {
        try {
            int flushed = 0;
            int remaining = serialPort.getInputBufferBytesCount();
            while (remaining > 0) {
                flushed += serialPort.readBytes(remaining, TIMEOUT_MS).length;
            }
            if (logger.isDebugEnabled() && flushed > 0) {
                logger.debug("flushed " + flushed + " bytes from read buffer");
            }
            return flushed;
        } catch (SerialPortTimeoutException e) {
            logger.error("timeout accessing device "+serialPort.getPortName());
        } catch (Exception e) {
            logger.error("error flushing read buffer", e);
        }
        return 0;
    }

    private void writeWord(int word) throws SerialPortException {
        serialPort.writeByte((byte)Binary.toUInt8low(word));
        serialPort.writeByte((byte)Binary.toUInt8high(word));
    }

    public synchronized void writeFrame(Frame frame) throws SerialPortException {
        serialPort.purgePort(SerialPort.PURGE_TXCLEAR|SerialPort.PURGE_RXCLEAR);
        FrameHeader header = new FrameHeader(frame);
        Crc8 crc8 = new Crc8();
        writeWord(header.getHeader());
        crc8.addWord(header.getHeader());
        if(header.getSizeBytesCount()>0) {
            serialPort.writeBytes(header.getSizeBytes());
            crc8.addBytes(header.getSizeBytes());
        }
        if(frame.getPayloadSize()>0) {
            serialPort.writeBytes(frame.getPayload());
            crc8.addBytes(frame.getPayload());
        }
        serialPort.writeByte((byte)(crc8.getCrc() & 0xff));
    }

    public String getPortName() {
        return serialPort.getPortName();
    }

    public int getSpeed() {
        return portBaudRate;
    }

    public boolean isOpened() {
        return serialPort.isOpened();
    }

    public synchronized Frame transaction(Frame request) {
        try {
            newHandling = true;
            receivedFrameLatch = new CountDownLatch(1);
            writeFrame(request);
            receivedFrameLatch.await(10, TimeUnit.SECONDS);
            return receivedFrameRef.getAndSet(null);
        } catch (Exception e) {
            logger.error("exception requesting "+request);
            return null;
        } finally {
            newHandling = false;
        }
    }
}
