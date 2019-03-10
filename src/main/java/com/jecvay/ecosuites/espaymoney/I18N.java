package com.jecvay.ecosuites.espaymoney;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class I18N {
    private static Logger logger = null;
    private static I18N ins = null;
    private ResourceBundle rb;

    private I18N(Locale locale) {
        String resource = "assets.espaymoney.i18n";
        this.rb = ResourceBundle.getBundle(resource, locale, new UTF8Control());
    }

    static public I18N getInstance() {
        if (ins == null) {
            ins = new I18N(Locale.getDefault());
        }
        return ins;
    }

    /////////////////

    static public void setLocale(Locale locale) {
        logger.info("Set language to {}", locale.toLanguageTag());
        ins = new I18N(locale);
    }

    public static void setLogger(Logger logger) {
        I18N.logger = logger;
    }

    public static String get(String key) {
        return getInstance().rb.getString(key);
    }
}

class UTF8Control extends ResourceBundle.Control {
    // https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle/4660195#4660195
    public ResourceBundle newBundle
            (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IOException
    {
        // The below is a copy of the default implementation.
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
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}