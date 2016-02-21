package com.mindpart.radio3.device;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.21
 */
public class ResponseCodeParser implements FrameParser<ResponseCodeParser.Code> {
    public enum Code {
        OK(0x03FF);

        private int value;

        Code(int value) {
            this.value = value;
        }

        public static Code of(int value) {
            for(Code code : values()) {
                if(code.value == value) {
                    return code;
                }
            }
            return null;
        }
    }

    @Override
    public boolean recognizes(Frame frame) {
        return Code.of(frame.getType())!=null;
    }

    @Override
    public Code parse(Frame frame) {
        return Code.of(frame.getType());
    }
}
