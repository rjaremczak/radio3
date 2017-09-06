package com.mindpart.radio3.ui;

import com.mindpart.utils.ResourceBundleUtils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.07.05
 */
public class BundleData {
    private ResourceBundle resourceBundle;

    public final String buttonConnect;
    public final String buttonDisconnect;
    public final String buttonStop;
    public final String buttonContinuous;

    public final String axisPower;
    public final String axisRelativePower;
    public final String axisVoltage;
    public final String axisRelativeVoltage;

    public BundleData(Locale locale) {
        resourceBundle = ResourceBundleUtils.getBundle("bundle", locale, "UTF-8");
        buttonConnect = resolve("button.connect");
        buttonDisconnect = resolve("button.disconnect");
        buttonStop = resolve("button.stop");
        buttonContinuous = resolve("button.continuous");

        axisPower = resolve("axis.power");
        axisRelativePower = resolve("axis.relativePower");
        axisVoltage = resolve("axis.voltage");
        axisRelativeVoltage = resolve("axis.relativeVoltage");
    }

    public String resolve(Object key) {
        return resourceBundle.getString(key.toString());
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
