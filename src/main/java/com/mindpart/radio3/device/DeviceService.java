package com.mindpart.radio3.device;

import com.mindpart.radio3.Status;
import com.mindpart.utils.Crc8;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import static com.mindpart.radio3.Status.OK;
import static com.mindpart.radio3.Status.error;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class DeviceService {
    private static final int MAX_ATTEMPTS = 5;
    private static final Logger logger = Logger.getLogger(DeviceService.class);

    private DataLink dataLink;
    private Status status = OK;

    private InvalidFrameParser invalidFrameParser = new InvalidFrameParser();
    private DeviceInfoParser deviceInfoParser = new DeviceInfoParser();
    private ResponseCodeParser responseCodeParser = new ResponseCodeParser();
    private FMeterParser fMeterParser = new FMeterParser();
    private LogarithmicProbeParser logarithmicProbeParser = new LogarithmicProbeParser();
    private LinearProbeParser linearProbeParser = new LinearProbeParser();
    private CompProbeParser compProbeParser = new CompProbeParser();

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
                status = OK;
            } catch (SerialPortException|SerialPortTimeoutException e) {
                status = error(e);
            }
        }

        logger.debug(status);
        return status;
    }

    public DeviceInfo readDeviceInfo() {
        return performRequest(DeviceInfoParser.READ_REQUEST, deviceInfoParser);
    }

    synchronized public Long readFrequency() {
        return performRequest(FMeterParser.READ_REQUEST, fMeterParser);
    }

    synchronized public boolean setVfoFrequency(int frequency) {
        return performRequest(new VfoSetFrequencyRequest(frequency), responseCodeParser) == OK;
    }

    synchronized public Double readLogProbe() {
        return performRequest(LogarithmicProbeParser.READ_REQUEST, logarithmicProbeParser);
    }

    synchronized public Double readLinProbe() {
        return performRequest(LinearProbeParser.READ_REQUEST, linearProbeParser);
    }

    synchronized public GainPhase readCompProbe() {
        return performRequest(CompProbeParser.READ_REQUEST, compProbeParser);
    }

    private Object performRequestRaw(Frame request, FrameParser frameParser) throws SerialPortException, Crc8.Error, SerialPortTimeoutException {
        dataLink.writeFrame(request);
        logger.debug("request: "+request);
        Frame response = dataLink.readFrame();
        logger.debug("response: "+response);
        if(frameParser.recognizes(response)) {
            status = OK;
            return frameParser.parse(response);
        } else if(invalidFrameParser.recognizes(response)) {
            status = error(invalidFrameParser.parse(response));
            return null;
        } else {
            status = error("unexpected frame " + response);
            return null;
        }
    }

    private <T> T performRequest(Frame request, FrameParser frameParser) {
        if(!isConnected()) {
            status = error("not connected");
            return null;
        }

        for(int attempt = 0; attempt<MAX_ATTEMPTS; attempt++) {
            try {
                Object result = performRequestRaw(request, frameParser);
                if(status.isOk() && result!=null) {
                    return (T)result;
                }
                logger.error(status);
            } catch (Exception e) {
                String str = "attempt "+(attempt+1)+"/"+MAX_ATTEMPTS+": "+e.getMessage();
                status = error(str);
                logger.error(str);
            } finally {
                dataLink.flushReadBuffer();
            }
        }
        return null;
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
