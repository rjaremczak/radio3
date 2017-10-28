package com.mindpart.radio3.ui;

import com.mindpart.util.ResourceBundleUtils;
import javafx.util.StringConverter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.07.05
 */
public class BundleData {
    private final ResourceBundle resourceBundle;
    private final StringConverter genericStringConverter;

    public final String buttonConnect;
    public final String buttonDisconnect;
    public final String buttonStart;
    public final String buttonStop;
    public final String buttonContinuous;
    public final String buttonGet;
    public final String buttonSet;

    public final String axisPower;
    public final String axisRelativePower;
    public final String axisVoltage;
    public final String axisRelativeVoltage;

    public final String textOn;
    public final String textOff;

    public BundleData(Locale locale) {
        resourceBundle = ResourceBundleUtils.getBundle("bundle", locale, "UTF-8");
        buttonConnect = resolve("button.connect");
        buttonDisconnect = resolve("button.disconnect");
        buttonStart = resolve("button.start");
        buttonStop = resolve("button.stop");
        buttonContinuous = resolve("button.continuous");
        buttonGet = resolve("button.get");
        buttonSet = resolve("button.set");

        axisPower = resolve("axis.power");
        axisRelativePower = resolve("axis.relativePower");
        axisVoltage = resolve("axis.voltage");
        axisRelativeVoltage = resolve("axis.relativeVoltage");

        textOn = resolve("text.on");
        textOff = resolve("text.off");

        genericStringConverter = new StringConverter() {
            @Override
            public String toString(Object object) {
                return resolve(object);
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        };
    }

    public String resolve(Object key) {
        return resourceBundle.getString(key.toString());
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public StringConverter getGenericStringConverter() {
        return genericStringConverter;
    }
}
