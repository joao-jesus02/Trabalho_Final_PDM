package com.example.bdroomcomcamera.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.databinding.ActivityFichaRacaBinding;
import com.example.bdroomcomcamera.entities.Favorito;
import com.example.bdroomcomcamera.entities.Racas;
import com.example.bdroomcomcamera.utils.ThemeUtils;

public class ActivityFichaRaca extends AppCompatActivity {

    private ActivityFichaRacaBinding binding;
    private AppDatabase db;
    private Racas raca;
    private int usuarioId;
    private int racaId;
    private boolean somenteLeitura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedMode(this);
        super.onCreate(savedInstanceState);
        binding = ActivityFichaRacaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        racaId = getIntent().getIntExtra("raca_id", -1);
        somenteLeitura = getIntent().getBooleanExtra("somente_leitura", false);
        db = DatabaseProvider.getDatabase(getApplicationContext());

        if (usuarioId == -1 || racaId == -1) {
            Toast.makeText(this, "Selecione uma criatura antes de abrir a ficha.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        carregarFicha();
        configurarModoDaTela();

        binding.btnSalvarFicha.setOnClickListener(view -> salvarFicha());
        binding.btnVoltarCadastro.setOnClickListener(view -> finish());
    }

    private void carregarFicha() {
        raca = db.racasDao().obterRacaDoUsuarioPorId(usuarioId, racaId);
        if (raca == null) {
            Toast.makeText(this, "Criatura não encontrada.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.txtTituloFicha.setText("Ficha - " + raca.getNomeRaca());
        binding.txtDescricaoFicha.setText(texto(raca.getDescricao()));
        binding.edtTipo.setText(texto(raca.getTipo()));
        binding.edtHabitat.setText(texto(raca.getHabitat()));
        binding.edtAlinhamento.setText(texto(raca.getAlinhamento()));
        binding.edtNivel.setText(numeroParaTexto(raca.getNivel()));
        binding.edtPontosVida.setText(numeroParaTexto(raca.getPontosVida()));
        binding.edtDefesa.setText(numeroParaTexto(raca.getDefesa()));
        binding.edtDificuldade.setText(numeroParaTexto(raca.getDificuldade()));
        binding.edtAtaques.setText(texto(raca.getAtaques()));
        binding.edtHabilidades.setText(texto(raca.getHabilidades()));
        binding.edtFraquezas.setText(texto(raca.getFraquezas()));
        binding.edtResistencias.setText(texto(raca.getResistencias()));
        binding.edtRecompensas.setText(texto(raca.getRecompensas()));
        binding.edtAnotacoesMestre.setText(texto(raca.getAnotacoesMestre()));
        binding.chkFavoritoFicha.setChecked(db.favoritoDao().estaFavoritada(usuarioId, raca.getId()));
    }

    private void configurarModoDaTela() {
        if (!somenteLeitura) {
            return;
        }

        binding.txtTituloFicha.setText("Visualizar ficha - " + raca.getNomeRaca());
        bloquearCampo(binding.edtTipo);
        bloquearCampo(binding.edtHabitat);
        bloquearCampo(binding.edtAlinhamento);
        bloquearCampo(binding.edtNivel);
        bloquearCampo(binding.edtPontosVida);
        bloquearCampo(binding.edtDefesa);
        bloquearCampo(binding.edtDificuldade);
        bloquearCampo(binding.edtAtaques);
        bloquearCampo(binding.edtHabilidades);
        bloquearCampo(binding.edtFraquezas);
        bloquearCampo(binding.edtResistencias);
        bloquearCampo(binding.edtRecompensas);
        bloquearCampo(binding.edtAnotacoesMestre);
        binding.chkFavoritoFicha.setEnabled(false);
        binding.btnSalvarFicha.setVisibility(View.GONE);
        binding.btnVoltarCadastro.setText("Voltar para pesquisa");
    }

    private void bloquearCampo(EditText campo) {
        campo.setFocusable(false);
        campo.setFocusableInTouchMode(false);
        campo.setCursorVisible(false);
        campo.setLongClickable(false);
        campo.setKeyListener(null);
    }

    private void salvarFicha() {
        if (raca == null) {
            return;
        }

        // Estes campos pertencem somente à ficha avançada da criatura.
        raca.setTipo(binding.edtTipo.getText().toString().trim());
        raca.setHabitat(binding.edtHabitat.getText().toString().trim());
        raca.setAlinhamento(binding.edtAlinhamento.getText().toString().trim());
        raca.setNivel(numero(binding.edtNivel));
        raca.setPontosVida(numero(binding.edtPontosVida));
        raca.setDefesa(numero(binding.edtDefesa));
        raca.setDificuldade(numero(binding.edtDificuldade));
        raca.setAtaques(binding.edtAtaques.getText().toString().trim());
        raca.setHabilidades(binding.edtHabilidades.getText().toString().trim());
        raca.setFraquezas(binding.edtFraquezas.getText().toString().trim());
        raca.setResistencias(binding.edtResistencias.getText().toString().trim());
        raca.setRecompensas(binding.edtRecompensas.getText().toString().trim());
        raca.setAnotacoesMestre(binding.edtAnotacoesMestre.getText().toString().trim());

        db.racasDao().atualizar(raca);
        salvarFavorito();

        Toast.makeText(this, "Ficha salva com sucesso.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void salvarFavorito() {
        if (binding.chkFavoritoFicha.isChecked()) {
            db.favoritoDao().favoritar(new Favorito(
                    usuarioId,
                    raca.getId(),
                    System.currentTimeMillis()
            ));
        } else {
            db.favoritoDao().desfavoritar(usuarioId, raca.getId());
        }
    }

    private int numero(EditText campo) {
        try {
            return Integer.parseInt(campo.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private String numeroParaTexto(int valor) {
        return valor <= 0 ? "" : String.valueOf(valor);
    }
}
