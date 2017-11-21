package com.mindpart.radio3.ui;

import com.mindpart.util.ResourceBundleUtils;
import javafx.util.StringConverter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.07.05
 */
public class UserInterface {
    private final ResourceBundle resourceBundle;
    private final StringConverter genericStringConverter;

    public final FrequencyFormat frequency = new FrequencyFormat();

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

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public StringConverter getGenericStringConverter() {
        return genericStringConverter;
    }


}
