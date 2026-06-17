package com.example.bdroomcomcamera.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.databinding.ActivityLoginBinding;
import com.example.bdroomcomcamera.entities.Usuario;
import com.example.bdroomcomcamera.utils.ImageUtils;
import com.example.bdroomcomcamera.utils.SoundUtils;
import com.example.bdroomcomcamera.utils.ThemeUtils;

public class ActivityLogin extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppDatabase db;
    private Usuario usuarioLogado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedMode(this);

        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.imgPerfil.setContentDescription("Foto do usuário logado");
        db = DatabaseProvider.getDatabase(getApplicationContext());

        int usuarioId = getIntent().getIntExtra("usuario_id", -1);
        if (usuarioId != -1) {
            usuarioLogado = db.usuarioDao().obterUsuario(usuarioId);
        }

        if (usuarioLogado == null) {
            Toast.makeText(this, "Faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.txtNome.setText(usuarioLogado.getNome());

        if (usuarioLogado.getFoto() != null) {
            Bitmap bitmap = ImageUtils.decodeByteArrayReduced(usuarioLogado.getFoto());
            if (bitmap != null) {
                binding.imgPerfil.setImageBitmap(bitmap);
            }
        }

        atualizarTextoModoEscuro();

        // FECHAR
        binding.btnFechar.setOnClickListener(view -> {

            SoundUtils.tocarClique(this);

            Fechar();

        });

        // ABRIR RAÇAS
        binding.btnCadastrarRacas.setOnClickListener(view -> {

            SoundUtils.tocarClique(this);

            CadastrarRacas();

        });

        binding.btnPesquisarPersonagens.setOnClickListener(view -> {

            SoundUtils.tocarClique(this);

            pesquisarPersonagens();

        });

        binding.btnModoEscuro.setOnClickListener(view -> {
            boolean modoEscuro = ThemeUtils.toggleManualTheme(this);
            Toast.makeText(
                    this,
                    modoEscuro ? "Tema escuro ativado no app." : "Tema claro ativado no app.",
                    Toast.LENGTH_SHORT
            ).show();
            atualizarTextoModoEscuro();
        });

        // ABRIR FRAGMENT
        binding.btnFragment.setOnClickListener(view -> {

            SoundUtils.tocarClique(this);

            Intent it =
                    new Intent(
                            ActivityLogin.this,
                            ActivityInfo.class
                    );
            it.putExtra("usuario_id", usuarioLogado.getId());

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
        it.putExtra("usuario_id", usuarioLogado.getId());

        startActivity(it);
    }

    private void pesquisarPersonagens() {
        Intent it =
                new Intent(
                        ActivityLogin.this,
                        ActivityPesquisaPersonagens.class
                );
        it.putExtra("usuario_id", usuarioLogado.getId());

        startActivity(it);
    }

    private void atualizarTextoModoEscuro() {
        binding.btnModoEscuro.setText(
                ThemeUtils.isDarkMode(this)
                        ? "Tema do app: escuro"
                        : "Tema do app: claro"
        );
    }
}
