package com.example.bdroomcomcamera.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bdroomcomcamera.R;

/**
 * Fragmento informativo do bestiário.
 * Ele é carregado pela ActivityInfo por meio do FragmentManager.
 */
public class InfoFragment extends Fragment {

    public InfoFragment() {
        super(R.layout.fragment_info);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }
}
