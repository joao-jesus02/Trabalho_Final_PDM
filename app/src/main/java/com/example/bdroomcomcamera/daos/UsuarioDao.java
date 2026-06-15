package com.example.bdroomcomcamera.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bdroomcomcamera.entities.Usuario;

@Dao
public interface UsuarioDao {
    @Insert
    void inserir(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE email = :email and senha = :senhaUsu")
    Usuario buscarUsuario(String email, String senhaUsu);

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    Usuario buscarUsuarioPorEmail(String email);

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario obterUsuario(int id);

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);

    @Update
    void atualizar(Usuario usuario);


}
