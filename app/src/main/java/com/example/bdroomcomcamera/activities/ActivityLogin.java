package com.example.bdroomcomcamera.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.databinding.ActivityLoginBinding;
import com.example.bdroomcomcamera.entities.Usuario;

public class ActivityLogin extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.imgPerfil.setContentDescription("Foto do usuário logado");

        Usuario usuario =
                (Usuario) getIntent()
                        .getSerializableExtra("usuario_logado");

        if (usuario != null) {

            binding.txtNome.setText(
                    usuario.getNome()
            );

            if(usuario.getFoto() != null){

                Bitmap bitmap =
                        BitmapFactory.decodeByteArray(
                                usuario.getFoto(),
                                0,
                                usuario.getFoto().length
                        );

                binding.imgPerfil.setImageBitmap(bitmap);
            }
        }

        // FECHAR
        binding.btnFechar.setOnClickListener(view -> {

            MediaPlayer mediaPlayer =
                    MediaPlayer.create(this, R.raw.click);

            mediaPlayer.start();

            Fechar();

        });

        // ABRIR RAÇAS
        binding.btnCadastrarRacas.setOnClickListener(view -> {

            MediaPlayer mediaPlayer =
                    MediaPlayer.create(this, R.raw.click);

            mediaPlayer.start();

            CadastrarRacas();

        });

        // ABRIR FRAGMENT
        binding.btnFragment.setOnClickListener(view -> {

            MediaPlayer mediaPlayer =
                    MediaPlayer.create(this, R.raw.click);

            mediaPlayer.start();

            Intent it =
                    new Intent(
                            ActivityLogin.this,
                            ActivityInfo.class
                    );

            startActivity(it);

        });
    }

    public void Fechar() {

        finish();
    }

    public void CadastrarRacas() {

        Intent it =
                new Intent(
                        ActivityLogin.this,
                        ActivityRacas.class
                );
        Usuario usuario =
                (Usuario) getIntent()
                        .getSerializableExtra("usuario_logado");
        if (usuario != null) {
            it.putExtra("usuario_id", usuario.getId());
        }

        startActivity(it);
    }
}
