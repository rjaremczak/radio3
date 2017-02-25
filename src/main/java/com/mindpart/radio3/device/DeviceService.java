package com.mindpart.radio3.device;

import com.mindpart.radio3.*;
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
import static com.mindpart.radio3.device.FrameCommand.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceService {
    private static final Logger logger = Logger.getLogger(DeviceService.class);

    private DataLink dataLink;
    private Map<FrameParser, BiConsumer<FrameParser, Frame>> bindings = new HashMap<>();
    private Consumer<AnalyserData> analyserDataHandler;
    private long framesReceived = 0;
    private long framesRecognized = 0;

    public <T extends FrameParser<U>, U> void registerBinding(T parser, BiConsumer<FrameParser, Frame> handler) {
        bindings.put(parser, handler);
    }

    public void frameHandler(Frame frame) {
        framesReceived++;

        for(Map.Entry<FrameParser, BiConsumer<FrameParser, Frame>> binding : bindings.entrySet()) {
            FrameParser parser = binding.getKey();
            if(parser.recognizes(frame)) {
                framesRecognized++;
                binding.getValue().accept(parser, frame);
                return;
            }
        }
        logger.warn("no binding for frame "+frame);
    }

    public Status connect(String portName) {
        framesReceived = 0;
        framesRecognized = 0;
        logger.debug("connecting to "+portName);
        dataLink = new DataLink(portName, this::frameHandler);
        try {
            dataLink.connect();
            return OK;
        } catch (SerialPortException|SerialPortTimeoutException e) {
            return error(e);
        }
    }

    public void disconnect() {
        if(dataLink!=null) {
            logger.debug("disconnect");
            dataLink.disconnect();
            dataLink = null;
        }
    }

    private void performRequest(Frame request) {
        try {
            dataLink.writeFrame(request);
            Thread.sleep(100);
        } catch (Exception e) {
            logger.error(e,e);
        }
    }

    public void startAnalyser(long freqStart, long freqStep, int numSteps, AnalyserDataSource source,
                              Consumer<AnalyserData> analyserDataHandler) {
        this.analyserDataHandler = analyserDataHandler;
        BinaryBuilder builder = new BinaryBuilder(14);
        builder.addUInt32(freqStart);
        builder.addUInt32(freqStep);
        builder.addUInt16(numSteps);
        builder.addUInt8(source.ordinal());
        performRequest(new Frame(FrameCommand.ANALYSER_REQUEST, builder.getBytes()));
    }

    public void setVfoFrequency(int frequency) {
        performRequest(new Frame(FrameCommand.VFO_SET_FREQ, Binary.fromUInt32(frequency)));
    }

    public void setHardwareRevision(HardwareRevision hardwareRevision) {
        performRequest(new Frame(FrameCommand.DEVICE_HARDWARE_REVISION, Binary.fromUInt8(hardwareRevision.getCode())));
    }

    public void setVfoType(VfoType vfoType) {
        performRequest(new Frame(FrameCommand.VFO_TYPE, Binary.fromUInt8(vfoType.getCode())));
    }

    public void setVfoAttenuator(VfoAttenuator vfoAttenuator) {
        performRequest(new Frame(FrameCommand.VFO_ATTENUATOR, Binary.fromUInt8(vfoAttenuator.getCode())));
    }

    public void setVfoOutput(VfoOut vfoOut) {
        performRequest(new Frame(vfoOut.getFrameCommand()));
    }

    public void setVnaMode(VnaMode vnaMode) {
        performRequest(new Frame(FrameCommand.VNA_MODE, Binary.fromUInt8(vnaMode.getCode())));
    }

    public void setVfoAmplifier(VfoAmplifier vfoAmplifier) {
        performRequest(new Frame(FrameCommand.VFO_AMPLIFIER, Binary.fromUInt8(vfoAmplifier.getCode())));
    }

    public List<String> availableSerialPorts() {
        return Arrays.asList(SerialPortList.getPortNames());
    }

    public void handleAnalyserData(AnalyserData analyserData) {
        analyserDataHandler.accept(analyserData);
    }

    public long getFramesReceived() {
        return framesReceived;
    }

    public long getFramesRecognized() {
        return framesRecognized;
    }

    public void requestDeviceState() {
        performRequest(new Frame(FrameCommand.DEVICE_GET_STATE));
    }

    public void requestVfoFrequency() {
        performRequest(new Frame(VFO_GET_FREQ));
    }

    public void requestDeviceInfo() {
        performRequest(new Frame(DEVICE_GET_INFO));
    }

    public void requestLogarithmicProbeSample() {
        performRequest(new Frame(LOGPROBE_GET));
    }

    public void requestLinearProbeSample() {
        performRequest(new Frame(LINPROBE_GET));
    }

    public void requestVnaProbeSample() {
        performRequest(new Frame(CMPPROBE_GET));
    }

    public void requestFMeterSample() {
        performRequest(new Frame(FMETER_GET));
    }

    public void requestMultipleProbesSample() {
        performRequest(new Frame(PROBES_GET));
    }

    public void startMultipleProbesSampling() {
        performRequest(new Frame(PROBES_START_SAMPLING));
    }

    public void stopMultipleProbesSampling() {
        performRequest(new Frame(PROBES_STOP_SAMPLING));
    }
}