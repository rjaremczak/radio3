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
        buttonConnect = getString("button.connect");
        buttonDisconnect = getString("button.disconnect");
        buttonStop = getString("button.stop");
        buttonContinuous = getString("button.continuous");

        axisPower = getString("axis.power");
        axisRelativePower = getString("axis.relativePower");
        axisVoltage = getString("axis.voltage");
        axisRelativeVoltage = getString("axis.relativeVoltage");
    }

    private String getString(String key) {
        return resourceBundle.getString(key);
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
