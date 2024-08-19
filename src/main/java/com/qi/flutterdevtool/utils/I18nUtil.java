package com.qi.flutterdevtool.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtil {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("strings.string", Locale.getDefault());

    public static String getString(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

}
