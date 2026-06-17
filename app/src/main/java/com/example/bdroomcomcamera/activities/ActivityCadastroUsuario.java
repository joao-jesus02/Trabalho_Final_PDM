package com.example.bdroomcomcamera.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.bdroomcomcamera.R;
import com.example.bdroomcomcamera.database.AppDatabase;
import com.example.bdroomcomcamera.database.DatabaseProvider;
import com.example.bdroomcomcamera.entities.Usuario;
import com.example.bdroomcomcamera.utils.ImageUtils;
import com.example.bdroomcomcamera.utils.PasswordStrengthUi;
import com.example.bdroomcomcamera.utils.SecurityUtils;
import com.example.bdroomcomcamera.utils.ThemeUtils;

import java.io.File;
import java.io.IOException;

public class ActivityCadastroUsuario extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE=1;
    ActivityResultLauncher<Uri> takePictureLauncher;
    ActivityResultLauncher<String> selecionarImagemLauncher;
    Uri imageUri;
    private EditText edtNome, edtEmail, edtSenha;
    private ImageView imgFoto;
    private ProgressBar progressForcaSenha;
    private TextView txtForcaSenha;
    private Bitmap fotoBitmap;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        imgFoto = findViewById(R.id.imgFoto);
        progressForcaSenha = findViewById(R.id.progressForcaSenha);
        txtForcaSenha = findViewById(R.id.txtForcaSenha);
        imgFoto.setContentDescription("Foto do usuário");
        Button btnCadaster = findViewById(R.id.btnCadastrar);

        db = DatabaseProvider.getDatabase(getApplicationContext());
        imageUri=createUri();
        registerPictureLauncher();
        registrarSeletorImagem();
        configurarIndicadorSenha();

        findViewById(R.id.btnTirarFoto).setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        findViewById(R.id.btnEscolherImagem).setOnClickListener(v -> selecionarImagemLauncher.launch("image/*"));
        btnCadaster.setOnClickListener(v -> cadastrarUsuario());
    }

    private void configurarIndicadorSenha() {
        edtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarForcaSenha(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void atualizarForcaSenha(String senha) {
        PasswordStrengthUi.updateForPassword(this, progressForcaSenha, txtForcaSenha, senha);
    }

    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() ||
                fotoBitmap == null) {
            Toast.makeText(this,
                    "Preencha todos os campos e adicione uma foto",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!SecurityUtils.isValidEmail(email)) {
            edtEmail.setError("E-mail inválido");
            return;
        }
        if (!SecurityUtils.isStrongPassword(senha)) {
            edtSenha.setError("Use 8+ caracteres com maiúscula, minúscula, número e símbolo");
            return;
        }
        Usuario usuarioExistente = db.usuarioDao().buscarPorEmail(email);
        if(usuarioExistente != null){
            Toast.makeText(this, "Email já cadastrado!", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] fotoBytes = ImageUtils.compressToJpeg(fotoBitmap);
        Usuario usuario = new Usuario(nome, email, SecurityUtils.hashPassword(senha), fotoBytes);
        db.usuarioDao().inserir(usuario);
        Toast.makeText(this, "Usuário cadastrado com sucesso!",
                Toast.LENGTH_LONG).show();
        limparCampos();
        finish();
    }

    private void limparCampos() {
        edtNome.setText("");
        edtEmail.setText("");
        edtSenha.setText("");
        imgFoto.setImageResource(R.drawable.outline_add_a_photo_24); // imagem default
        fotoBitmap = null;
    }

    public void capturarImagem(View view) {
        checkCameraPermissionAndOpenCamera();
    }
    private Uri createUri(){
        File imageFile=new File(getApplicationContext().getFilesDir(),"camera_photo.jpg");
        return FileProvider.getUriForFile(getApplicationContext(),
                "com.example.bdroomcomcamera.fileprovider", imageFile );
    }
    private void registerPictureLauncher() {
        takePictureLauncher=registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean o) {
                        try{
                            if(o){
                                imgFoto.setImageURI(null);
                                imgFoto.setImageURI(imageUri);

                                fotoBitmap = ImageUtils.decodeUriReduced(getContentResolver(), imageUri);
                            }
                        }catch (Exception exception){
                            exception.getStackTrace();
                        }
                    }
                }
        );
    }

    private void registrarSeletorImagem() {
        selecionarImagemLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) {
                        return;
                    }

                    try {
                        fotoBitmap = ImageUtils.decodeUriReduced(getContentResolver(), uri);
                        imgFoto.setImageBitmap(fotoBitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Não foi possível carregar a imagem.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void checkCameraPermissionAndOpenCamera() {
        if(ActivityCompat.checkSelfPermission(ActivityCadastroUsuario.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ActivityCadastroUsuario.this,
                    new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }else{
            Toast.makeText(this,"Check Permission Granted", Toast.LENGTH_SHORT).show();
            takePictureLauncher.launch(imageUri);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Request Permission Granted",Toast.LENGTH_SHORT).show();
                takePictureLauncher.launch(imageUri);
            }else{
                Toast.makeText(this,"Request Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
