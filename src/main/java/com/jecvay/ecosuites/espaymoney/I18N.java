package com.jecvay.ecosuites.espaymoney;

import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class I18N {
    private static Logger logger = null;
    private static I18N ins = null;
    private static ESPayMoney esp;
    // private static String assetsPath = "assets.espaymoney";
    private static String assetsName = "i18n";

    private ResourceBundle rb;

    private I18N(Locale locale) {
        this.rb = getCustomBundle(assetsName, locale);
    }

    static public I18N getInstance() {
        if (ins == null) {
            ins = new I18N(Locale.getDefault());
        }
        return ins;
    }

    private ResourceBundle getCustomBundle(String name, Locale locale) {
        Path configDir = esp.getConfigDir();
        Path langDir = configDir.resolve("lang");
        if (!Files.exists(langDir)) {
            try {
                Files.createDirectories(langDir);
            } catch (IOException e) {
                logger.error("Failed to create langDir directory: {}", e);
            }
        }
        String fileName;
        if (locale.toString().equals("en_US")) {
            fileName = name + ".properties";
        } else {
            fileName = String.join("_", name, locale.toString()) + ".properties";
        }
        Path filePath = langDir.resolve(fileName);
        if (!Files.exists(filePath)) {
            try {
                esp.getContainer().getAsset(fileName).get().copyToFile(filePath);
            } catch (IOException e) {
                logger.error("Failed to copy language file {}", e);
            }
        }

        File file = new File(langDir.toString());
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{file.toURI().toURL()};
        } catch (MalformedURLException e) {
            logger.error("Failed to create resource loader {}", e);
        }
        ClassLoader loader = new URLClassLoader(urls);
        return ResourceBundle.getBundle(name, locale, loader, new UTF8Control());
    }

    /////////////////

    static public void setLocale(Locale locale) {
        logger.info("Set language to {}", locale.toLanguageTag());
        ins = new I18N(locale);
    }

    static public void setPlugin(ESPayMoney esp) {
        I18N.esp = esp;
    }

    public static void setLogger(Logger logger) {
        I18N.logger = logger;
    }

    public static String getString(String key, Object... args) {
        if (getInstance().rb.containsKey(key)) {
            String value = getInstance().rb.getString(key);
            return MessageFormat.format(value, args);
        } else {
            return key;
        }
    }

    public static Text getText(String key, Object... args) {
        String filledValue = getString(key, args);
        if (filledValue.equals(key)) {
            return Text.of(key);
        } else {
            return TextSerializers.FORMATTING_CODE.deserialize(filledValue);
        }
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