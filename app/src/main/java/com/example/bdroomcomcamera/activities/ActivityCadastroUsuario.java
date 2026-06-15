package com.example.bdroomcomcamera.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.bdroomcomcamera.utils.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ActivityCadastroUsuario extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE=1;
    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri imageUri;
    private EditText edtNome, edtEmail, edtSenha;
    private ImageView imgFoto;
    private Bitmap fotoBitmap;
    private AppDatabase db;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        imgFoto = findViewById(R.id.imgFoto);
        imgFoto.setContentDescription("Foto do usuário");
        Button btnCadaster = findViewById(R.id.btnCadastrar);

        db = DatabaseProvider.getDatabase(getApplicationContext());
        imageUri=createUri();
        registerPictureLauncher();

        btnCadaster.setOnClickListener(v -> cadastrarUsuario());
    }

    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() ||
                fotoBitmap == null) {
            Toast.makeText(this,
                    "Preencha todos os campos e tire uma foto",
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
        // Converter a imagem para byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fotoBytes = stream.toByteArray();
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

                                fotoBitmap = MediaStore.Images.Media.getBitmap(
                                        getContentResolver(),
                                        imageUri);
                            }
                        }catch (Exception exception){
                            exception.getStackTrace();
                        }
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
