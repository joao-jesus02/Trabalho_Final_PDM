package com.example.bdroomcomcamera.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.utils.ThemeUtils;

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
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Button btnModoEscuro = view.findViewById(R.id.btnModoEscuro);

        atualizarTextoModoEscuro(btnModoEscuro);

        btnModoEscuro.setOnClickListener(v -> {
            boolean modoEscuro = ThemeUtils.toggleManualTheme(requireContext());
            atualizarTextoModoEscuro(btnModoEscuro);
            Toast.makeText(
                    requireContext(),
                    modoEscuro ? "Tema escuro ativado no app." : "Tema claro ativado no app.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        return view;
    }

    private void atualizarTextoModoEscuro(Button botao) {
        botao.setText(
                ThemeUtils.isDarkMode(requireContext())
                        ? "Tema do app: escuro"
                        : "Tema do app: claro"
        );
    }
}
