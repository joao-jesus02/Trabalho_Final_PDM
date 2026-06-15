package com.example.bdroomcomcamera.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.databinding.ActivityRecuperarSenhaBinding;
import com.example.bdroomcomcamera.entities.Usuario;
import com.example.bdroomcomcamera.utils.SecurityUtils;

import java.util.Locale;
import java.util.Random;

public class ActivityRecuperarSenha extends AppCompatActivity {
    private ActivityRecuperarSenhaBinding binding; // Se estiver usando ViewBinding
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarSenhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = DatabaseProvider.getDatabase(getApplicationContext());
        binding.btnSalvarNovaSenha.setOnClickListener(v -> {

            MediaPlayer mediaPlayer =
                    MediaPlayer.create(this, R.raw.click);

            mediaPlayer.start();

            RedefinirSenha();
        });
    }

    private void RedefinirSenha() {

        String email =
                binding.edtEmailRecuperar.getText().toString().trim();

        String novaSenha =
                binding.edtNovaSenha.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(this,
                    "Informe o e-mail cadastrado!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!SecurityUtils.isValidEmail(email)) {
            binding.edtEmailRecuperar.setError("E-mail inválido");
            return;
        }

        Usuario usuario =
                db.usuarioDao().buscarPorEmail(email);

        if(usuario == null){
            Toast.makeText(this,
                    "Usuário não encontrado!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (novaSenha.isEmpty()) {
            String codigo = String.format(Locale.US, "%06d", new Random().nextInt(1000000));
            usuario.setCodigoRecuperacaoHash(SecurityUtils.hashPassword(codigo));
            db.usuarioDao().atualizar(usuario);
            Toast.makeText(this,
                    "Código de recuperação: " + codigo + ". Digite codigo:novaSenha no campo Nova Senha.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String[] partes = novaSenha.split(":", 2);
        if (partes.length != 2) {
            Toast.makeText(this,
                    "Use o formato codigo:novaSenha",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String codigo = partes[0].trim();
        String senhaNova = partes[1];

        if (!SecurityUtils.matchesPassword(codigo, usuario.getCodigoRecuperacaoHash())) {
            Toast.makeText(this,
                    "Código de recuperação inválido!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!SecurityUtils.isStrongPassword(senhaNova)) {
            binding.edtNovaSenha.setError("Use 8+ caracteres com maiúscula, minúscula, número e símbolo");
            return;
        }

        usuario.setSenha(SecurityUtils.hashPassword(senhaNova));
        usuario.setCodigoRecuperacaoHash(null);

        db.usuarioDao().atualizar(usuario);

        Toast.makeText(this,
                "Senha alterada com sucesso!",
                Toast.LENGTH_LONG).show();

        finish();
    }
}
