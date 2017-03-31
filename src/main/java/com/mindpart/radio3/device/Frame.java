package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;
import com.mindpart.utils.BinaryIterator;
import com.mindpart.utils.Crc8;
import jssc.SerialPort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.16
 */
public class Frame {
    private FrameCommand command;
    private byte[] payload;

    public Frame(FrameCommand command) {
        this(command, null);
    }

    public Frame(FrameCommand command, byte[] payload) {
        this.command = command;
        this.payload = payload;
    }

    public FrameCommand getCommand() {
        return command;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getPayloadSize() {
        return payload != null ? payload.length : 0;
    }

    @Override
    public String toString() {
        return command + ":"+getPayloadSize();
    }

    public BinaryIterator binaryIterator() {
        return new BinaryIterator(payload);
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FrameHeader header = new FrameHeader(this);
        Crc8 crc8 = new Crc8();

        os.write(Binary.fromUInt16(header.getHeader()));
        crc8.process(header.getHeader());

        if(header.getSizeBytesCount()>0) {
            os.write(header.getSizeBytes());
            crc8.process(header.getSizeBytes());
        }

        if(getPayloadSize() > 0) {
            os.write(getPayload());
            crc8.process(getPayload());
        }

        os.write((byte)(crc8.getCrc() & 0xff));
        return os.toByteArray();
    }

    public static final Frame fromBytes(byte[] bytes) throws Crc8.Error {
        int readPos = 0;

        Crc8 crc8 = new Crc8();
        crc8.process(bytes, readPos, 2);
        FrameHeader header = FrameHeader.fromCode(Binary.toUInt16(bytes, readPos));
        readPos += 2;

        if(header.getSizeBytesCount()>0) {
            int numSizeBytes = Math.max(2, header.getSizeBytesCount());
            header.setSizeBytes(bytes, readPos, numSizeBytes);
            crc8.process(bytes, readPos, numSizeBytes);
            readPos += numSizeBytes;
        }

        int payloadStart = readPos;
        crc8.process(bytes, payloadStart, header.getPayloadSize());
        readPos += header.getPayloadSize();

        int receivedCrc = bytes[readPos] & 0xff;
        if(receivedCrc != crc8.getCrc()) {
            throw new Crc8.Error(receivedCrc, crc8.getCrc());
        }

        return new Frame(header.getCommand(), Arrays.copyOfRange(bytes, payloadStart, payloadStart+header.getPayloadSize()));
    }
}
