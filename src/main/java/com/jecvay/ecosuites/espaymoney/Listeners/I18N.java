package com.jecvay.ecosuites.espaymoney.Listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    private static Logger logger = null;
    private static I18N ins = null;
    private ResourceBundle rb;

    private I18N(Locale locale) {
        String resource = "assets.espaymoney.i18n";
        this.rb = ResourceBundle.getBundle(resource, locale);
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
