package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import com.mindpart.radio3.ui.ComplexProbeController;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;

import java.util.*;
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
    private Map<Class<? extends FrameParser>, FrameParser> parsers = new HashMap<>();
    private Map<FrameParser, BiConsumer<FrameParser, Frame>> handlers = new HashMap<>();

    public DeviceService() throws InstantiationException, IllegalAccessException {
        registerParser(DeviceInfoParser.class);
        registerParser(StatusCodeParser.class);
        registerParser(FMeterParser.class);
        registerParser(LogarithmicProbeParser.class);
        registerParser(LinearProbeParser.class);
        registerParser(ComplexProbeParser.class);
        registerParser(VfoReadFrequencyParser.class);
        registerParser(ProbesParser.class);
    }

    private <T extends FrameParser> void registerParser(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        parsers.put(clazz, clazz.newInstance());
    }

    public void registerHandler(Class<? extends FrameParser> clazz, BiConsumer<FrameParser, Frame> handler) {
        FrameParser parser = parsers.get(clazz);
        if(parser == null) {
            throw new IllegalArgumentException("parser "+clazz+" not registered");
        }

        handlers.put(parser, handler);
    }

    public void frameHandler(Frame frame) {
        for(Map.Entry<Class<? extends FrameParser>, FrameParser> entry : parsers.entrySet()) {
            FrameParser parser = entry.getValue();
            if(parser.recognizes(frame)) {
                BiConsumer<FrameParser, Frame> handler = handlers.get(parser);
                if(handler!=null) {
                    handler.accept(parser, frame);
                    return;
                } else {
                    logger.warn("no handler for "+entry.getKey());
                }
            }
        }
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
                dataLink.connect(this::frameHandler);
                status = OK;
            } catch (SerialPortException|SerialPortTimeoutException e) {
                status = error(e);
            }
        }

        logger.debug(status);
        return status;
    }

    synchronized public void deviceInfo() {
        performRequest(DeviceInfoParser.GET);
    }

    synchronized public void vfoGetFrequency() {
        performRequest(VfoReadFrequencyParser.SAMPLE);
    }

    synchronized public void vfoSetFrequency(int frequency) {
        performRequest(new VfoSetFrequency(frequency));
    }

    synchronized public void fMeterGet() {
        performRequest(FMeterParser.SAMPLE);
    }

    synchronized public void linearProbeGet() {
        performRequest(LinearProbeParser.SAMPLE);
    }

    synchronized public void complexProbeGet() {
        performRequest(ComplexProbeParser.SAMPLE);
    }

    synchronized public void logarithmicProbeGet() {
        performRequest(LogarithmicProbeParser.SAMPLE);
    }

    synchronized public void probesGet() { performRequest(ProbesParser.SAMPLE);}

    synchronized public void probesStartSampling() { performRequest(ProbesParser.START_SAMPLING);}

    synchronized public void probesStopSampling() { performRequest(ProbesParser.STOP_SAMPLING);}

    private void performRequest(Frame request) {
        try {
            dataLink.writeFrame(request);
            Thread.sleep(100);
        } catch (Exception e) {
            status = error(e.getMessage());
            logger.error(e);
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
