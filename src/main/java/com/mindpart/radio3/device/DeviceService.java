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

    public <T extends FrameParser<U>, U> void registerBinding(T parser, BiConsumer<FrameParser, Frame> handler) {
        bindings.put(parser, handler);
    }

    public void frameHandler(Frame frame) {
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

    synchronized public Status connect(String portName) {
        logger.debug("connect to "+portName);
        if(isConnected()) {
            status = error("already connected");
        } else {
            dataLink = new DataLink(portName);
            try {
                dataLink.connect();
                dataLink.attachFrameListener(this::frameHandler);
                status = OK;
            } catch (SerialPortException|SerialPortTimeoutException e) {
                status = error(e);
            }
        }

        logger.debug(status);
        return status;
    }

    synchronized public void getDeviceInfo() {
        performRequest(DeviceInfoParser.GET);
    }

    synchronized public void getDeviceState() { performRequest(DeviceStateParser.GET); }

    synchronized public void getVfoFrequency() {
        performRequest(VfoReadFrequencyParser.SAMPLE);
    }

    synchronized public void setVfoFrequency(int frequency) {
        performRequest(new Frame(FrameCommand.VFO_SET_FREQ, Binary.fromUInt32(frequency)));
    }

    synchronized public void getFMeter() {
        performRequest(FMeterParser.SAMPLE);
    }

    synchronized public void getLinearProbe() {
        performRequest(LinearProbeParser.SAMPLE);
    }

    synchronized public void getComplexProbe() {
        performRequest(ComplexProbeParser.SAMPLE);
    }

    synchronized public void getLogarithmicProbe() {
        performRequest(LogarithmicProbeParser.SAMPLE);
    }

    synchronized public void getProbes() { performRequest(ProbesParser.SAMPLE);}

    synchronized public void startProbesSampling() { performRequest(ProbesParser.START_SAMPLING);}

    synchronized public void stopProbesSampling() { performRequest(ProbesParser.STOP_SAMPLING);}

    synchronized public void startAnalyser(long freqStart, long freqStep, int numSteps, int stepWaitMs, AnalyserData.Source source) {
        BinaryBuilder builder = new BinaryBuilder(14);
        builder.addUInt32(freqStart);
        builder.addUInt32(freqStep);
        builder.addUInt16(numSteps);
        builder.addUInt16(stepWaitMs);
        builder.addUInt16(source.ordinal());
        performRequest(new Frame(FrameCommand.ANALYSER_REQUEST, builder.getBytes()));
    }

    private void performRequest(Frame request) {
        try {
            dataLink.writeFrame(request);
            Thread.sleep(100);
        } catch (Exception e) {
            status = error(e.getMessage());
            logger.error(e,e);
        }
    }

    synchronized public Status disconnect() {
        logger.debug("disconnect");
        if(!isConnected()) {
            status = error("not connected");
        } else {
            dataLink.disconnect();
            dataLink = null;
            status = OK;
        }
        logger.debug(status);
        return status;
    }

    synchronized public List<String> availableSerialPorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    public Status getStatus() {
        return status;
    }
}
