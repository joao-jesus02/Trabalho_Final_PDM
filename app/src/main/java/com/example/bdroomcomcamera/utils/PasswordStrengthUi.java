package com.example.bdroomcomcamera.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.bdroomcomcamera.R;

public final class PasswordStrengthUi {

    private PasswordStrengthUi() {
    }

    public static void updateForPassword(
            Context context,
            ProgressBar progressBar,
            TextView label,
            String password
    ) {
        int score = SecurityUtils.passwordStrengthScore(password);
        int colorRes;
        String message;

        if (password == null || password.isEmpty()) {
            colorRes = R.color.password_weak;
            message = "Use 8+ caracteres, maiuscula, minuscula, numero e simbolo";
        } else if (score <= 2) {
            colorRes = R.color.password_weak;
            message = "Senha fraca";
        } else if (score <= 4) {
            colorRes = R.color.password_medium;
            message = "Senha media: ainda faltam requisitos";
        } else {
            colorRes = R.color.password_strong;
            message = "Senha forte";
        }

        apply(context, progressBar, label, score, colorRes, message);
    }

    public static void updateForRecoveryInput(
            Context context,
            ProgressBar progressBar,
            TextView label,
            String input
    ) {
        String password = extractRecoveryPassword(input);
        int score = SecurityUtils.passwordStrengthScore(password);
        int colorRes;
        String message;

        if (input == null || input.trim().isEmpty()) {
            colorRes = R.color.app_text_secondary;
            message = "Deixe em branco para gerar codigo, ou use codigo:novaSenha";
        } else if (!input.contains(":")) {
            colorRes = R.color.password_weak;
            message = "Use o formato codigo:novaSenha";
        } else if (password.isEmpty()) {
            colorRes = R.color.password_weak;
            message = "Digite a nova senha apos os dois-pontos";
        } else if (score <= 2) {
            colorRes = R.color.password_weak;
            message = "Senha fraca";
        } else if (score <= 4) {
            colorRes = R.color.password_medium;
            message = "Senha media: ainda faltam requisitos";
        } else {
            colorRes = R.color.password_strong;
            message = "Senha forte";
        }

        apply(context, progressBar, label, score, colorRes, message);
    }

    private static String extractRecoveryPassword(String input) {
        if (input == null) {
            return "";
        }
        String[] parts = input.split(":", 2);
        return parts.length == 2 ? parts[1] : "";
    }

    private static void apply(
            Context context,
            ProgressBar progressBar,
            TextView label,
            int score,
            int colorRes,
            String message
    ) {
        int color = ContextCompat.getColor(context, colorRes);
        progressBar.setProgress(score);
        progressBar.setProgressTintList(ColorStateList.valueOf(color));
        label.setText(message);
        label.setTextColor(color);
    }
}
