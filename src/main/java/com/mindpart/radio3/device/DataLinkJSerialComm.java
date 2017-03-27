package com.mindpart.radio3.device;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.mindpart.utils.Binary;
import com.mindpart.utils.Crc8;
import jssc.SerialPortException;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.25
 */
public class DataLinkJSerialComm implements DataLink {
    private static final Logger logger = Logger.getLogger(DataLinkJSerialComm.class);

    private static final int TIMEOUT_MS = 2000;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    private static final int PARITY = SerialPort.NO_PARITY;
    private static final int BAUD_RATE = 115200;

    private SerialPort comPort;
    private Consumer<Frame> frameHandler;
    private Consumer<DataLinkException> exceptionHandler;

    public DataLinkJSerialComm(Consumer<Frame> frameHandler, Consumer<DataLinkException> exceptionHandler) {
        this.frameHandler = frameHandler;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public synchronized void connect(String portName) throws Exception {
        logger.debug("open port "+portName);
        this.comPort = SerialPort.getCommPort(portName);
        if(comPort.openPort()) {
            logger.debug("opened");
            comPort.setComPortParameters(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
            comPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING|TIMEOUT_WRITE_BLOCKING, TIMEOUT_MS, 0);
            comPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent serialPortEvent) {
                    try {
                        Frame frame = readFrame();
                        frameHandler.accept(frame);
                    } catch (Exception e) {
                        exceptionHandler.accept(new DataLinkException(comPort.isOpen(), e));
                    }
                }
            });
            logger.debug("configured");
        } else {
            logger.error("port opening failed");
        }
    }

    private Frame readFrame() throws Crc8.Error {
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
        if(comPort.bytesAvailable()!=0) {
            logger.warn("remaining "+comPort.bytesAvailable()+" bytes in RX buffer");
        }

        return new Frame(header.getCommand(), payload);
    }

    private byte[] readBytes(int num) {
        ByteBuffer buf = ByteBuffer.allocate(num);
        int remaining = num;
        while(remaining > 0) {
            int size = Math.min(remaining, comPort.bytesAvailable());
            if(size<0) { exceptionHandler.accept(new DataLinkException(comPort.isOpen(), null)); }
            byte[] chunk = new byte[size];
            comPort.readBytes(chunk, size);
            buf.put(chunk);
            remaining -= size;
        }
        return buf.array();
    }

    @Override
    public synchronized void disconnect() {
        logger.debug("remove data listener");
        comPort.removeDataListener();
        logger.debug("close port");
        comPort.closePort();
        logger.debug("closed");
    }

    private void writeWord(int word) throws SerialPortException {
        byte[] buf = {(byte)Binary.toUInt8low(word), (byte)Binary.toUInt8high(word) };
        comPort.writeBytes(buf, buf.length);
    }

    @Override
    public synchronized void writeFrame(Frame frame) throws Exception {
        FrameHeader header = new FrameHeader(frame);
        Crc8 crc8 = new Crc8();
        writeWord(header.getHeader());
        crc8.addWord(header.getHeader());
        if(header.getSizeBytesCount()>0) {
            comPort.writeBytes(header.getSizeBytes(), header.getSizeBytesCount());
            crc8.addBytes(header.getSizeBytes());
        }
        if(frame.getPayloadSize()>0) {
            comPort.writeBytes(frame.getPayload(), frame.getPayloadSize());
            crc8.addBytes(frame.getPayload());
        }

        byte[] crc = { (byte)(crc8.getCrc() & 0xff) };
        comPort.writeBytes(crc, crc.length);
    }

    @Override
    public String getPortName() {
        return comPort!=null ? comPort.getSystemPortName() : "no port selected";
    }

    @Override
    public boolean isOpened() {
        return comPort!=null && comPort.isOpen();
    }

    @Override
    public synchronized Frame request(Frame request) {
        return null;
    }

    @Override
    public List<String> availablePorts() {
        return Arrays.stream(SerialPort.getCommPorts())
                .map(SerialPort::getSystemPortName)
                .filter(n -> !n.startsWith("cu."))
                .collect(Collectors.toList());
    }
}