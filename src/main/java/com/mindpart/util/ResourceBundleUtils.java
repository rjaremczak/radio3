package com.mindpart.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.26
 */
public final class ResourceBundleUtils {
    private ResourceBundleUtils() {}

    static final class Control extends ResourceBundle.Control {
        private String charsetName;

        public Control(String charsetName) {
            super();
            this.charsetName = charsetName;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, charsetName));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

    public static final ResourceBundle getBundle(String bundleName, Locale locale, String charsetName) {
        return ResourceBundle.getBundle("bundle", locale, new Control(charsetName));
    }
}
