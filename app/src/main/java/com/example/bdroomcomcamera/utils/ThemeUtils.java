package com.example.bdroomcomcamera.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    private static final String PREFS = "preferencias_tema";
    private static final String KEY_THEME_MODE = "tema_app";
    private static final String MODE_SYSTEM = "system";
    private static final String MODE_LIGHT = "light";
    private static final String MODE_DARK = "dark";

    private ThemeUtils() {
    }

    public static void applySavedMode(Context context) {
        String mode = getSavedMode(context);
        if (MODE_DARK.equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (MODE_LIGHT.equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            syncWithDevice();
        }
    }

    public static boolean isDarkMode(Context context) {
        int modoAtual = context.getResources()
                .getConfiguration()
                .uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return modoAtual == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean toggleManualTheme(Context context) {
        boolean novoModoEscuro = !isDarkMode(context);
        String novoModo = novoModoEscuro ? MODE_DARK : MODE_LIGHT;
        prefs(context)
                .edit()
                .putString(KEY_THEME_MODE, novoModo)
                .apply();

        AppCompatDelegate.setDefaultNightMode(
                novoModoEscuro
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
        return novoModoEscuro;
    }

    public static boolean isManualMode(Context context) {
        return !MODE_SYSTEM.equals(getSavedMode(context));
    }

    public static void syncWithDevice() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private static String getSavedMode(Context context) {
        return prefs(context).getString(KEY_THEME_MODE, MODE_SYSTEM);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
