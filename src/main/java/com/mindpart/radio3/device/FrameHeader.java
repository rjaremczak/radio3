package com.mindpart.radio3.device;

import com.mindpart.utils.Binary;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.16
 */

/**

 Frame structure
 ---------------

 First 16 bits is the header which has following structure:

 bits 12-15:    frame format
 bits 0-11:     command - used by application

 Payload can directly follow the header or follow size data, that depends on frame format code:

 format A:
 00 - 13: direct size of payload in bytes

 format B:
 14 - one byte size (-14) followed by payload (size of 0 means 14 bytes)

 format C:
 15 - two byte size (-269) followed by payload (size of 0 means 269 bytes)

 - additional crc8 appended at the end of the frame

 */

class FrameHeader {
    private int format;
    private FrameCommand command;
    private int size;

    FrameHeader(int format, FrameCommand command) {
        this.format = format;
        this.command = command;

        if(isFormatA()) {
            size = format;
        }
    }

    static FrameHeader fromCode(int header) {
        int format = (header >> 12) & 0x0f;
        FrameCommand command = FrameCommand.fromCode(header & 0x7ff);
        return new FrameHeader(format, command);
    }

    static FrameHeader fromBytes(byte[] bytes) {
        return fromCode(Binary.toUInt16(bytes));
    }

    FrameHeader(Frame frame) {
        this.command = frame.getCommand();
        int payloadSize = frame.getPayloadSize();
        if(payloadSize <= 13) {
            format = payloadSize;
            size = payloadSize;
        } else if(payloadSize <= 269) {
            format = 14;
            size = payloadSize - 14;
        } else if(payloadSize <= 65804) {
            format = 15;
            size = payloadSize - 270;
        } else {
            throw new IllegalArgumentException("payload too large");
        }
    }

    int getHeader() {
        return ((format & 0x0f) << 12) | command.getCode();
    }

    boolean isFormatA() {
        return format < 14;
    }

    boolean isFormatB() {
        return format == 14;
    }

    boolean isFormatC() {
        return format == 15;
    }

    int getSizeBytesCount() {
        if(isFormatA()) {
            return 0;
        } else if(isFormatB()) {
            return 1;
        } else {
            return 2;
        }
    }

    void setSizeBytes(byte... bytes) {
        if(isFormatB()) {
            size = bytes[0] & 0xff;
        } else if(isFormatC()){
            size = Binary.toUInt16(bytes);
        } else {
            size = 0;
        }
    }

    void setSizeBytes(byte[] bytes, int pos, int length) {
        if(isFormatB()) {
            size = bytes[pos] & 0xff;
        } else if(isFormatC()){
            size = Binary.toUInt16(bytes, pos);
        }
    }

    public int getPayloadSize() {
        if(isFormatA()) {
            return size;
        } else if(isFormatB()) {
            return size + 14;
        } else {
            return size + 270;
        }
    }

    public byte[] getSizeBytes() {
        if(isFormatB()) {
            return new byte[]{ (byte)Binary.toUInt8low(size) };
        } else if(isFormatC()) {
            return new byte[]{ (byte)Binary.toUInt8low(size), (byte)Binary.toUInt8high(size) };
        } else {
            return null;
        }
    }

    public FrameCommand getCommand() {
        return command;
    }
}
