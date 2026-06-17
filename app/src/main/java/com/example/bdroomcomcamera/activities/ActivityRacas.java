package com.example.bdroomcomcamera.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.databinding.ActivityRacasBinding;
import com.example.bdroomcomcamera.entities.Racas;
import com.example.bdroomcomcamera.utils.ImageUtils;
import com.example.bdroomcomcamera.utils.SoundUtils;
import com.example.bdroomcomcamera.utils.ThemeUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ActivityRacas extends AppCompatActivity {

    private static final String CHANNEL_ID = "racas_channel";
    private static final int REQUEST_NOTIFICATIONS = 30;
    private static final int REQUEST_RECORD_AUDIO = 31;

    private ActivityRacasBinding binding;

    private AppDatabase db;

    private byte[] imagemSelecionada = null;

    private List<Racas> listaRacas;

    private ArrayAdapter<Racas> adapter;

    private Racas racaParaEditar = null;
    private int usuarioId;
    private ActivityResultLauncher<String[]> selecionarAudioLauncher;
    private MediaRecorder mediaRecorder;
    private android.media.MediaPlayer audioPlayer;
    private boolean gravandoAudio = false;
    private String audioUriSelecionado;
    private String audioNomeSelecionado;
    private boolean mostrandoFavoritos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.applySavedMode(this);
        super.onCreate(savedInstanceState);

        binding = ActivityRacasBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.imgPreview.setContentDescription("Imagem da raça");

        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        if (usuarioId == -1) {
            Toast.makeText(this, "Faça login novamente para cadastrar raças.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db = DatabaseProvider.getDatabase(getApplicationContext());
        criarCanalNotificacao();
        pedirPermissaoNotificacao();
        registrarSeletorDeAudio();

        atualizarLista();

        // BUSCA
        binding.edtBuscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                buscarRacas(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // SELECIONAR IMAGEM
        binding.btnSelecionarImagem.setOnClickListener(view -> {

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );

            startActivityForResult(intent, 100);

        });

        // SALVAR
        binding.btnSalvarRaca.setOnClickListener(view -> {

            SoundUtils.tocarClique(this);

            salvar();

        });

        // EXCLUIR
        binding.btnEcluir.setOnClickListener(view -> {

            SoundUtils.tocarClique(this);

            excluir();

        });

        binding.btnSelecionarAudio.setOnClickListener(view ->
                selecionarAudioLauncher.launch(new String[]{"audio/mpeg", "audio/wav", "audio/ogg", "audio/*"}));

        binding.btnGravarAudio.setOnClickListener(view -> alternarGravacaoAudio());

        binding.btnReproduzirAudio.setOnClickListener(view -> reproduzirAudio());

        binding.btnFicha.setOnClickListener(view -> abrirTelaFicha());
        binding.btnFavoritos.setOnClickListener(view -> alternarFavoritos());
        binding.btnDados.setOnClickListener(view -> abrirRoladorDados());
        binding.btnEncontro.setOnClickListener(view -> abrirGeradorEncontro());

        // CLICAR ITEM DA LISTA
        binding.lvRacas.setOnItemClickListener((parent, view, position, id) -> {

            racaParaEditar = listaRacas.get(position);

            binding.edtNomeRaca.setText(racaParaEditar.getNomeRaca());
            binding.edtDescricaoRaca.setText(racaParaEditar.getDescricao());

            if (racaParaEditar.getImagem() != null) {

                Bitmap bitmap = ImageUtils.decodeByteArrayReduced(racaParaEditar.getImagem());

                if (bitmap != null) {
                    binding.imgPreview.setImageBitmap(bitmap);
                }

                imagemSelecionada = racaParaEditar.getImagem();
            } else {
                binding.imgPreview.setImageResource(android.R.drawable.ic_menu_camera);
                imagemSelecionada = null;
            }
            audioUriSelecionado = racaParaEditar.getAudioUri();
            audioNomeSelecionado = racaParaEditar.getAudioNome();
            atualizarNomeAudio();
            binding.btnSalvarRaca.setText("Atualizar");

        });
    }

    private void salvar() {

        String nome =
                binding.edtNomeRaca.getText().toString().trim();

        String desc =
                binding.edtDescricaoRaca.getText().toString().trim();

        if (nome.isEmpty() || desc.isEmpty()) {

            Toast.makeText(
                    this,
                    "Preencha os campos!",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (imagemSelecionada == null && desc.startsWith("https://")) {
            baixarImagemDaDescricao(desc);
            return;
        }

        // INSERT
        if (racaParaEditar == null) {

            Racas novaRaca = new Racas(
                    nome,
                    desc,
                    imagemSelecionada
            );
            novaRaca.setUsuarioId(usuarioId);
            novaRaca.setAudioUri(audioUriSelecionado);
            novaRaca.setAudioNome(audioNomeSelecionado);

            long novoId = db.racasDao().inserir(novaRaca);
            novaRaca.setId((int) novoId);

            Toast.makeText(
                    this,
                    "Criatura salva na lista!",
                    Toast.LENGTH_LONG
            ).show();
            notificar("Raça cadastrada", nome + " foi salva no banco.");

        }

        // UPDATE
        else {

            racaParaEditar.setNomeRaca(nome);

            racaParaEditar.setDescricao(desc);

            racaParaEditar.setImagem(imagemSelecionada);
            racaParaEditar.setAudioUri(audioUriSelecionado);
            racaParaEditar.setAudioNome(audioNomeSelecionado);

            db.racasDao().atualizar(racaParaEditar);

            Toast.makeText(
                    this,
                    "Criatura atualizada!",
                    Toast.LENGTH_SHORT
            ).show();
            notificar("Raça atualizada", nome + " foi atualizada no banco.");
        }

        mostrandoFavoritos = false;
        binding.btnFavoritos.setText("Fav.");
        binding.edtBuscar.setText("");
        atualizarLista();
        limparCampos();
    }

    public void excluir() {

        if (racaParaEditar == null) {
            if (formularioPreenchido()) {
                Toast.makeText(
                        this,
                        "Para cadastrar essa criatura, toque em Salvar.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            Toast.makeText(
                    this,
                    "Selecione uma criatura!",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(this)

                .setTitle("Excluir Registro")

                .setMessage(
                        "Deseja excluir: "
                                + racaParaEditar.getNomeRaca()
                                + " ?"
                )

                .setPositiveButton("Sim", (dialog, which) -> {

                    excluirRaca();

                    Toast.makeText(
                            this,
                            "Removido com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                })

                .setNegativeButton("Cancelar", null)

                .show();
    }

    public void excluirRaca() {

        if (racaParaEditar == null) {

            Toast.makeText(
                    this,
                    "Selecione uma criatura!",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.racasDao().deletar(racaParaEditar);

        limparCampos();

        atualizarLista();
    }

    // BUSCA
    private void buscarRacas(String texto) {

        if (texto.isEmpty()) {

            atualizarLista();

            return;
        }

        listaRacas =
                db.racasDao().buscarPorNomeDoUsuario(usuarioId, texto);

        adapter = criarAdapterComImagem(listaRacas);

        binding.lvRacas.setAdapter(adapter);
    }

    // LISTAR
    private void atualizarLista() {

        listaRacas =
                db.racasDao().obterTodasRacasDoUsuario(usuarioId);

        adapter = criarAdapterComImagem(listaRacas);

        binding.lvRacas.setAdapter(adapter);
    }

    // LIMPAR CAMPOS
    private void limparCampos() {

        binding.edtNomeRaca.setText("");

        binding.edtDescricaoRaca.setText("");

        binding.imgPreview.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        imagemSelecionada = null;
        audioUriSelecionado = null;
        audioNomeSelecionado = null;
        atualizarNomeAudio();

        racaParaEditar = null;

        binding.btnSalvarRaca.setText("Salvar");
    }

    private boolean formularioPreenchido() {
        return !binding.edtNomeRaca.getText().toString().trim().isEmpty()
                || !binding.edtDescricaoRaca.getText().toString().trim().isEmpty()
                || imagemSelecionada != null
                || audioUriSelecionado != null;
    }

    private ArrayAdapter<Racas> criarAdapterComImagem(List<Racas> racas) {
        return new ArrayAdapter<Racas>(this, R.layout.item_raca, R.id.txtItemRaca, racas) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                Racas raca = getItem(position);
                TextView texto = view.findViewById(R.id.txtItemRaca);
                ImageView imagem = view.findViewById(R.id.imgItemRaca);

                if (raca != null) {
                    String favorito = db.favoritoDao().estaFavoritada(usuarioId, raca.getId()) ? "★ " : "";
                    String nivel = raca.getNivel() > 0 ? "  Nível " + raca.getNivel() : "";
                    texto.setText(favorito + raca.getNomeRaca() + nivel);
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

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == 100
                && resultCode == RESULT_OK
                && data != null) {

            Uri uri = data.getData();

            try {

                Bitmap bitmap = ImageUtils.decodeUriReduced(getContentResolver(), uri);

                binding.imgPreview.setImageBitmap(bitmap);

                imagemSelecionada = ImageUtils.compressToJpeg(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    // Usa o seletor de documentos moderno e mantém permissão de leitura da URI escolhida.
    private void registrarSeletorDeAudio() {
        selecionarAudioLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null) {
                        return;
                    }
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (SecurityException ignored) {
                        // Alguns provedores não oferecem permissão persistente, mas a URI ainda pode ser usada na sessão.
                    }
                    audioUriSelecionado = uri.toString();
                    audioNomeSelecionado = obterNomeDoAudio(uri);
                    atualizarNomeAudio();
                }
        );
    }

    // O mesmo botão inicia e encerra a gravação para não adicionar controles desnecessários.
    private void alternarGravacaoAudio() {
        if (gravandoAudio) {
            pararGravacaoAudio();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO
            );
            return;
        }

        iniciarGravacaoAudio();
    }

    private void iniciarGravacaoAudio() {
        File pasta = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (pasta == null) {
            pasta = getFilesDir();
        }
        File arquivo = new File(pasta, "audio_raca_" + System.currentTimeMillis() + ".m4a");

        try {
            mediaRecorder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    ? new MediaRecorder(this)
                    : new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(arquivo.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

            gravandoAudio = true;
            audioUriSelecionado = Uri.fromFile(arquivo).toString();
            audioNomeSelecionado = arquivo.getName();
            binding.btnGravarAudio.setText("Parar Gravação");
            binding.txtNomeAudio.setText("Gravando: " + audioNomeSelecionado);
        } catch (IOException | RuntimeException e) {
            liberarGravador();
            Toast.makeText(this, "Não foi possível iniciar a gravação.", Toast.LENGTH_SHORT).show();
        }
    }

    private void pararGravacaoAudio() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
            }
            Toast.makeText(this, "Áudio gravado com sucesso.", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            audioUriSelecionado = null;
            audioNomeSelecionado = null;
            Toast.makeText(this, "Gravação muito curta ou inválida.", Toast.LENGTH_SHORT).show();
        } finally {
            liberarGravador();
            gravandoAudio = false;
            binding.btnGravarAudio.setText("Gravar Áudio");
            atualizarNomeAudio();
        }
    }

    private void liberarGravador() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    // Reproduz tanto URIs selecionadas pelo usuário quanto arquivos gravados pelo aplicativo.
    private void reproduzirAudio() {
        if (audioUriSelecionado == null || audioUriSelecionado.isEmpty()) {
            Toast.makeText(this, "Selecione ou grave um áudio primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }

        liberarPlayer();
        audioPlayer = new android.media.MediaPlayer();
        try {
            audioPlayer.setDataSource(this, Uri.parse(audioUriSelecionado));
            audioPlayer.setOnPreparedListener(android.media.MediaPlayer::start);
            audioPlayer.setOnCompletionListener(player -> liberarPlayer());
            audioPlayer.prepareAsync();
        } catch (IOException | SecurityException e) {
            liberarPlayer();
            Toast.makeText(this, "Não foi possível reproduzir o áudio.", Toast.LENGTH_SHORT).show();
        }
    }

    private void liberarPlayer() {
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
    }

    private String obterNomeDoAudio(Uri uri) {
        try (Cursor cursor = getContentResolver().query(
                uri,
                new String[]{OpenableColumns.DISPLAY_NAME},
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int indice = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (indice >= 0) {
                    return cursor.getString(indice);
                }
            }
        }
        return "Áudio selecionado";
    }

    private void atualizarNomeAudio() {
        binding.txtNomeAudio.setText(
                audioNomeSelecionado == null || audioNomeSelecionado.isEmpty()
                        ? "Nenhum áudio associado"
                        : audioNomeSelecionado
        );
    }

    private void abrirTelaFicha() {
        if (racaParaEditar == null) {
            Toast.makeText(this, "Selecione uma criatura da lista para abrir a ficha.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ActivityFichaRaca.class);
        intent.putExtra("usuario_id", usuarioId);
        intent.putExtra("raca_id", racaParaEditar.getId());
        startActivity(intent);
    }

    private EditText criarCampo(LinearLayout formulario, String dica, String valor, boolean numerico) {
        EditText campo = new EditText(this);
        campo.setHint(dica);
        campo.setText(valor == null || "0".equals(valor) ? "" : valor);
        if (numerico) {
            campo.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        } else {
            campo.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        formulario.addView(campo);
        return campo;
    }

    private int numero(EditText campo) {
        try {
            return Integer.parseInt(campo.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void alternarFavoritos() {
        mostrandoFavoritos = !mostrandoFavoritos;
        binding.btnFavoritos.setText(mostrandoFavoritos ? "Todos" : "Fav.");
        listaRacas = mostrandoFavoritos
                ? db.favoritoDao().obterFavoritasDoUsuario(usuarioId)
                : db.racasDao().obterTodasRacasDoUsuario(usuarioId);
        adapter = criarAdapterComImagem(listaRacas);
        binding.lvRacas.setAdapter(adapter);
    }

    // Rolador genérico para testes, ataques e decisões rápidas durante a sessão.
    private void abrirRoladorDados() {
        LinearLayout conteudo = new LinearLayout(this);
        conteudo.setOrientation(LinearLayout.VERTICAL);
        conteudo.setPadding(32, 16, 32, 16);

        Spinner dado = new Spinner(this);
        List<String> dados = Arrays.asList("d4", "d6", "d8", "d10", "d12", "d20", "d100");
        dado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dados));
        conteudo.addView(dado);

        EditText quantidade = criarCampo(conteudo, "Quantidade de dados", "1", true);
        EditText bonus = criarCampo(conteudo, "Bônus", "0", true);

        new AlertDialog.Builder(this)
                .setTitle("Rolador de Dados")
                .setView(conteudo)
                .setPositiveButton("Rolar", (dialog, which) -> {
                    int lados = Integer.parseInt(dado.getSelectedItem().toString().substring(1));
                    int qtd = Math.max(1, numero(quantidade));
                    int soma = numero(bonus);
                    StringBuilder detalhes = new StringBuilder();
                    Random random = new Random();
                    for (int i = 0; i < qtd; i++) {
                        int resultado = random.nextInt(lados) + 1;
                        soma += resultado;
                        if (i > 0) detalhes.append(" + ");
                        detalhes.append(resultado);
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("Resultado: " + soma)
                            .setMessage(detalhes.toString())
                            .setPositiveButton("OK", null)
                            .show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Usa a dificuldade cadastrada na ficha para sugerir quantidade de inimigos.
    private void abrirGeradorEncontro() {
        if (racaParaEditar == null) {
            Toast.makeText(this, "Selecione uma criatura para gerar o encontro.", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout conteudo = new LinearLayout(this);
        conteudo.setOrientation(LinearLayout.VERTICAL);
        conteudo.setPadding(32, 16, 32, 16);
        EditText jogadores = criarCampo(conteudo, "Quantidade de jogadores", "4", true);
        EditText nivelGrupo = criarCampo(conteudo, "Nível médio do grupo", "1", true);

        new AlertDialog.Builder(this)
                .setTitle("Gerar encontro")
                .setView(conteudo)
                .setPositiveButton("Calcular", (dialog, which) -> {
                    int poderGrupo = Math.max(1, numero(jogadores)) * Math.max(1, numero(nivelGrupo));
                    int dificuldadeCriatura = Math.max(1, racaParaEditar.getDificuldade());
                    int quantidade = Math.max(1, poderGrupo / dificuldadeCriatura);
                    String avaliacao = quantidade >= 4 ? "Encontro numeroso" :
                            quantidade >= 2 ? "Encontro equilibrado" : "Encontro difícil";
                    new AlertDialog.Builder(this)
                            .setTitle(avaliacao)
                            .setMessage("Sugestão: " + quantidade + "x " + racaParaEditar.getNomeRaca()
                                    + "\nDificuldade da criatura: " + dificuldadeCriatura)
                            .setPositiveButton("OK", null)
                            .show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarGravacaoAudio();
            } else {
                Toast.makeText(this, "Permissão de microfone necessária para gravar.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (gravandoAudio) {
            pararGravacaoAudio();
        }
        liberarPlayer();
        liberarGravador();
        super.onDestroy();
    }

    private void baixarImagemDaDescricao(String endereco) {
        new Thread(() -> {
            try (InputStream inputStream = new URL(endereco).openStream()) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Imagem HTTPS inválida.", Toast.LENGTH_SHORT).show());
                    return;
                }
                imagemSelecionada = ImageUtils.compressToJpeg(bitmap);
                runOnUiThread(this::salvar);
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Não foi possível baixar a imagem HTTPS.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Cadastros de raças",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void pedirPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= 33 &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
        }
    }

    private void notificar(String titulo, String mensagem) {
        if (Build.VERSION.SDK_INT >= 33 &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }
}
