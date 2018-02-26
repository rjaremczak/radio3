package com.mindpart.radio3.ui;

import com.mindpart.ui.DoubleSpinner;
import com.mindpart.ui.FxUtils;
import com.mindpart.util.ResourceBundleUtils;
import javafx.scene.Parent;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.07.05
 */
public class UserInterface {
    private static final String FREQUENCY_FORMAT_MHZ = "##0.000";
    private static final double FREQUENCY_MIN_MHZ = 0.1;
    private static final double FREQUENCY_MAX_MHZ = 70;
    private static final double FREQUENCY_STEP_MHZ = 0.01;

    private final ResourceBundle resourceBundle;
    private final StringConverter genericStringConverter;

    public final DecimalFormat decimal = new DecimalFormat();
    public final ImpedanceFormat impedance = new ImpedanceFormat();
    public final DateTimeFormatter timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public UserInterface(Locale locale) {
        resourceBundle = ResourceBundleUtils.getBundle("bundle", locale, "UTF-8");
        genericStringConverter = new StringConverter() {
            @Override
            public String toString(Object object) {
                return text(object);
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        };
    }

    public String text(Object key) {
        return resourceBundle.getString(key.toString());
    }

    public StringConverter getGenericStringConverter() {
        return genericStringConverter;
    }

    public void initFrequencyField(DoubleSpinner doubleSpinner) {
        doubleSpinner.setDecimalFormat(FREQUENCY_FORMAT_MHZ);
        doubleSpinner.getEditor().setPrefColumnCount(6);
        doubleSpinner.getDoubleValueFactory().setValue(FREQUENCY_MIN_MHZ);
        doubleSpinner.getDoubleValueFactory().setAmountToStepBy(FREQUENCY_STEP_MHZ);
    }

    public void initFrequencyFieldWithRanges(DoubleSpinner doubleSpinner) {
        initFrequencyField(doubleSpinner);
        doubleSpinner.getDoubleValueFactory().setMin(FREQUENCY_MIN_MHZ);
        doubleSpinner.getDoubleValueFactory().setMax(FREQUENCY_MAX_MHZ);
    }

    public Parent loadFXml(Object controller, String fxml) {
        return FxUtils.loadFXml(controller, fxml, resourceBundle);
    }

    public String formatBoolean(boolean b) {
        return resourceBundle.getString(b ? "text.yes" : "text.no");
    }
}
