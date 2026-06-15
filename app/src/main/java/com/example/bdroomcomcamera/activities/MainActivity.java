package com.example.bdroomcomcamera.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.databinding.ActivityMainBinding;
import com.example.bdroomcomcamera.entities.Usuario;
import com.example.bdroomcomcamera.utils.SecurityUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = DatabaseProvider.getDatabase(getApplicationContext());

        // BOTÃO CADASTRAR
        binding.btnAbrirCadastro.setOnClickListener(v -> {
            tocarSom();
            Cadastrar();
        });

        // BOTÃO LOGIN
        binding.btnLogin.setOnClickListener(v -> {
            tocarSom();
            Entrar();
        });

        // TEXTO ESQUECI SENHA
        binding.txtEsqueci.setOnClickListener(v -> {
            tocarSom();
            AlterarSenha();
        });
    }

    // 🔊 MÉTODO PARA TOCAR SOM (CORRIGIDO)
    private void tocarSom() {

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.click);

        if (mediaPlayer != null) {

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
            });

            mediaPlayer.start();

        } else {
            Toast.makeText(this, "Erro ao tocar som", Toast.LENGTH_SHORT).show();
        }
    }

    public void Cadastrar() {
        Intent it = new Intent(MainActivity.this, ActivityCadastroUsuario.class);
        startActivity(it);
    }

    public void AlterarSenha() {
        Intent it = new Intent(MainActivity.this, ActivityRecuperarSenha.class);
        startActivity(it);
    }

    private void Entrar() {

        String email = binding.edtEmail1.getText().toString().trim();
        String senha = binding.edtSenha1.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this,
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!SecurityUtils.isValidEmail(email)) {
            binding.edtEmail1.setError("E-mail inválido");
            return;
        }

        Usuario usuario = db.usuarioDao().buscarUsuarioPorEmail(email);

        if (usuario != null && SecurityUtils.matchesPassword(senha, usuario.getSenha())) {
            if (!SecurityUtils.isSha256Hash(usuario.getSenha())) {
                usuario.setSenha(SecurityUtils.hashPassword(senha));
                db.usuarioDao().atualizar(usuario);
            }

            Intent it = new Intent(MainActivity.this, ActivityLogin.class);
            it.putExtra("usuario_logado", usuario);
            startActivity(it);

        } else {

            Toast.makeText(this,
                    "Usuário ou senha inválidos!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.edtEmail1.setText("");
        binding.edtSenha1.setText("");
    }
}
