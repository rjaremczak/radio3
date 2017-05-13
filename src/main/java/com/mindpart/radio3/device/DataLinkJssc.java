package com.mindpart.radio3.device;

import com.mindpart.utils.Crc8;
import jssc.*;
import jssc.SerialPort;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static jssc.SerialPort.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.08
 */

public class DataLinkJssc implements DataLink {
    private static final Logger logger = Logger.getLogger(DataLinkJssc.class);

    private static final int TIMEOUT_MS = 1000;
    private static final int DATA_BITS = DATABITS_8;
    private static final int STOP_BITS = STOPBITS_1;
    private static final int PARITY = PARITY_NONE;
    private static final int BAUD_RATE = BAUDRATE_115200;

    private SerialPort serialPort;

    private volatile AtomicReference<Frame> receivedFrameRef = new AtomicReference<>();
    private volatile AtomicReference<Exception> receiveExceptionRef = new AtomicReference<>();
    private volatile CountDownLatch receivedFrameLatch;

    public synchronized void connect(String portName) throws SerialPortException, SerialPortTimeoutException {
        this.serialPort = new SerialPort(portName);
        if(serialPort!=null && serialPort.openPort()) {
            logger.debug("port opened");
            flushBuffers();
            serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
            serialPort.setFlowControlMode(FLOWCONTROL_NONE);
            attachEventListener();
        } else {
            logger.error("error opening port");
        }
    }

    private void attachEventListener() throws SerialPortException {
        serialPort.addEventListener(event -> {
            if(serialPort==null || !serialPort.isOpened()) return;

            if(event.isRXCHAR()) {
                eventRxChar();
            } else if(event.isTXEMPTY()) {
                eventTxEmpty();
            }
        });
    }

    private void eventRxChar() {
        try {
            Crc8 crc = new Crc8();
            byte[] headerBytes = readBytes(2);
            crc.process(headerBytes);

            FrameHeader header = FrameHeader.fromBytes(headerBytes);
            if(header.getSizeBytesCount() > 0) {
                byte[] sizeBytes = readBytes(header.getSizeBytesCount());
                header.setSizeBytes(sizeBytes);
                crc.process(sizeBytes);
            }

            byte[] payloadBytes = readBytes(header.getPayloadSize());
            int receivedCrc = readBytes(1)[0] & 0xff;
            crc.process(payloadBytes);

            if(crc.getCrc() == receivedCrc) {
                final Frame frame = new Frame(header.getCommand(), payloadBytes);
                receivedFrameRef.set(frame);
            } else {
                flushBuffers();
                receiveExceptionRef.set(new Crc8.Error(crc.getCrc(), receivedCrc));
            }
        } catch (Exception e) {
            receiveExceptionRef.set(e);
        } finally {
            receivedFrameLatch.countDown();
        }
    }

    private void eventTxEmpty() {
        logger.debug("txEmpty");
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

    private void flushBuffers() throws SerialPortException {
        serialPort.purgePort(PURGE_RXCLEAR|PURGE_TXCLEAR);
    }

    public synchronized void disconnect() {
        try {
            serialPort.removeEventListener();
            flushBuffers();
            serialPort.closePort();
            serialPort = null;
            logger.debug("port closed");
        } catch (SerialPortException e) {
            logger.error("exception closing port "+serialPort.getPortName(),e);
        }
    }

    public String getPortName() {
        return serialPort.getPortName();
    }

    public boolean isOpened() {
        return serialPort!=null && serialPort.isOpened();
    }

    public synchronized Response request(Frame request) {
        try {
            flushBuffers();
            long t0 = System.currentTimeMillis();
            receivedFrameLatch = new CountDownLatch(1);
            serialPort.writeBytes(request.toBytes());
            receivedFrameLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            Exception exception = receiveExceptionRef.getAndSet(null);
            if(exception != null) {
                if(logger.isDebugEnabled()) {
                    logger.debug("request: "+request+" exception "+exception, exception);
                }
                return Response.error(exception);
            }
            Frame response = receivedFrameRef.getAndSet(null);
            if(logger.isDebugEnabled()) {
                logger.debug("request: "+request+" -> "+(response!=null ? response : "no response")+" in "+(System.currentTimeMillis()-t0)+" ms");
            }
            return response!=null ? Response.success(response) : Response.error("no response");
        } catch (Exception e) {
            return Response.error(e);
        }
    }

    public List<String> availablePorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    public String toString() {
        return "jSSC " + SerialNativeInterface.getLibraryVersion()+" ("+SerialNativeInterface.getNativeLibraryVersion()+")";
    }
}
