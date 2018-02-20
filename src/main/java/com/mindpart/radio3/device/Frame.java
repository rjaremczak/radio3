package com.mindpart.radio3.device;

import com.mindpart.bin.Binary;
import com.mindpart.bin.BinaryIterator;
import com.mindpart.bin.Crc8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.16
 */
public class Frame {
    private FrameCmd command;
    private byte[] payload;

    public Frame(FrameCmd command) {
        this(command, null);
    }

    public Frame(FrameCmd command, byte[] payload) {
        this.command = command;
        this.payload = payload;
    }

    public FrameCmd getCommand() {
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
        StringBuilder sb = new StringBuilder(command.toString());
        sb.append(":");

        int payloadSize = getPayloadSize();
        if(payloadSize > 0 && payloadSize <= 4) {
            sb.append(Arrays.toString(payload));
        } else if(payloadSize > 4) {
            sb.append(Integer.toString(payloadSize));
            sb.append(" B");
        }
        return sb.toString();
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
}
