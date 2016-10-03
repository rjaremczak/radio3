package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import com.mindpart.utils.Binary;
import com.mindpart.utils.BinaryBuilder;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.mindpart.radio3.Status.OK;
import static com.mindpart.radio3.Status.error;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceService {
    private static final Logger logger = Logger.getLogger(DeviceService.class);

    private DataLink dataLink;
    private Status status = OK;
    private Map<FrameParser, BiConsumer<FrameParser, Frame>> bindings = new HashMap<>();
    private Consumer<AnalyserData> analyserDataHandler;
    private Consumer<AnalyserState> analyserStateHandler;
    private long framesReceived;

    public <T extends FrameParser<U>, U> void registerBinding(T parser, BiConsumer<FrameParser, Frame> handler) {
        bindings.put(parser, handler);
    }

    public void frameHandler(Frame frame) {
        framesReceived++;

        for(Map.Entry<FrameParser, BiConsumer<FrameParser, Frame>> binding : bindings.entrySet()) {
            FrameParser parser = binding.getKey();
            if(parser.recognizes(frame)) {
                binding.getValue().accept(parser, frame);
                return;
            }
        }
        logger.warn("no binding for frame "+frame);
    }

    public boolean isConnected() {
        return dataLink!=null && dataLink.isConnected();
    }

    public Status connect(String portName) {
        framesReceived = 0;
        disconnect();
        logger.debug("connecting to "+portName);
        dataLink = new DataLink(portName);
        try {
            dataLink.connect();
            dataLink.attachFrameListener(this::frameHandler);
            status = OK;
        } catch (SerialPortException|SerialPortTimeoutException e) {
            status = error(e);
        }
        logger.debug(status);
        return status;
    }

    public void setVfoFrequency(int frequency) {
        performRequest(new Frame(FrameCommand.VFO_SET_FREQ, Binary.fromUInt32(frequency)));
    }

    public void startAnalyser(long freqStart, long freqStep, int numSteps, int stepWaitMs,
                              AnalyserData.Source source,
                              Consumer<AnalyserData> analyserDataHandler,
                              Consumer<AnalyserState> analyserStateHandler) {
        this.analyserDataHandler = analyserDataHandler;
        this.analyserStateHandler = analyserStateHandler;
        BinaryBuilder builder = new BinaryBuilder(14);
        builder.addUInt32(freqStart);
        builder.addUInt32(freqStep);
        builder.addUInt16(numSteps);
        builder.addUInt16(stepWaitMs);
        builder.addUInt16(source.ordinal());
        performRequest(new Frame(FrameCommand.ANALYSER_REQUEST, builder.getBytes()));
    }

    public void performRequest(Frame request) {
        try {
            dataLink.writeFrame(request);
            Thread.sleep(100);
        } catch (Exception e) {
            status = error(e.getMessage());
            logger.error(e,e);
        }
    }

    public Status disconnect() {
        if(dataLink!=null) {
            logger.debug("disconnect");
            dataLink.disconnect();
            dataLink = null;
            status = OK;
        }
        logger.debug(status);
        return status;
    }

    public List<String> availableSerialPorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    public void handleAnalyserData(AnalyserData analyserData) {
        analyserDataHandler.accept(analyserData);
    }

    public void handleAnalyserState(AnalyserState analyserState) {
        analyserStateHandler.accept(analyserState);
    }

    public long getFramesReceived() {
        return framesReceived;
    }
}