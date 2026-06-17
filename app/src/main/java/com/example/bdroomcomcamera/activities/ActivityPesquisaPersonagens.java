package com.example.bdroomcomcamera.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.databinding.ActivityPesquisaPersonagensBinding;
import com.example.bdroomcomcamera.entities.Racas;
import com.example.bdroomcomcamera.utils.ImageUtils;
import com.example.bdroomcomcamera.utils.ThemeUtils;

import java.util.List;

public class ActivityPesquisaPersonagens extends AppCompatActivity {

    private ActivityPesquisaPersonagensBinding binding;
    private AppDatabase db;
    private List<Racas> personagens;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedMode(this);
        super.onCreate(savedInstanceState);
        binding = ActivityPesquisaPersonagensBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        db = DatabaseProvider.getDatabase(getApplicationContext());

        if (usuarioId == -1) {
            Toast.makeText(this, "Faça login novamente para pesquisar personagens.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        carregarPersonagens("");

        binding.edtPesquisarPersonagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                carregarPersonagens(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.lvPersonagens.setOnItemClickListener((parent, view, position, id) -> abrirFicha(personagens.get(position)));
        binding.btnVoltarPesquisa.setOnClickListener(view -> finish());
    }

    private void carregarPersonagens(String textoBusca) {
        String texto = textoBusca == null ? "" : textoBusca.trim();
        personagens = texto.isEmpty()
                ? db.racasDao().obterTodasRacasDoUsuario(usuarioId)
                : db.racasDao().buscarPorNomeDoUsuario(usuarioId, texto);

        binding.lvPersonagens.setAdapter(criarAdapter(personagens));
        binding.txtQuantidadeResultados.setText("Personagens encontrados: " + personagens.size());
    }

    private ArrayAdapter<Racas> criarAdapter(List<Racas> lista) {
        return new ArrayAdapter<Racas>(this, R.layout.item_raca, R.id.txtItemRaca, lista) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                Racas raca = getItem(position);
                TextView texto = view.findViewById(R.id.txtItemRaca);
                ImageView imagem = view.findViewById(R.id.imgItemRaca);

                if (raca != null) {
                    String tipo = raca.getTipo() == null || raca.getTipo().isEmpty()
                            ? ""
                            : " - " + raca.getTipo();
                    texto.setText(raca.getNomeRaca() + tipo);

                    byte[] bytes = raca.getImagem();
                    if (bytes != null && bytes.length > 0) {
                        Bitmap bitmap = ImageUtils.decodeThumbnail(bytes);
                        imagem.setImageBitmap(bitmap);
                    } else {
                        imagem.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                }

                return view;
            }
        };
    }

    private void abrirFicha(Racas raca) {
        Intent intent = new Intent(this, ActivityFichaRaca.class);
        intent.putExtra("usuario_id", usuarioId);
        intent.putExtra("raca_id", raca.getId());
        intent.putExtra("somente_leitura", true);
        startActivity(intent);
    }
}
