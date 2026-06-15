package com.example.bdroomcomcamera.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.fragments.InfoFragment;

public class ActivityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        new InfoFragment())
                .commit();
    }
}