package com.example.bdroomcomcamera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.bestiario_icon);
        logo.setContentDescription("Logo do aplicativo");
        layout.addView(logo, new LinearLayout.LayoutParams(180, 180));

        TextView titulo = new TextView(this);
        titulo.setText(getString(R.string.app_name));
        titulo.setTextSize(24);
        titulo.setGravity(Gravity.CENTER);
        layout.addView(titulo);

        setContentView(layout);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1200);
    }
}
