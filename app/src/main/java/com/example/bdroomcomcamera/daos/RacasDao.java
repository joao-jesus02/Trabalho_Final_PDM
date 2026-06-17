package com.example.bdroomcomcamera.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bdroomcomcamera.entities.Racas;

import java.util.List;

@Dao
public interface RacasDao {

    @Insert
    long inserir(Racas raca);

    @Update
    void atualizar(Racas raca);

    @Query("SELECT * FROM racas")
    List<Racas> obterTodasRacas();

    @Query("SELECT * FROM racas WHERE nomeRaca like :nomeRaca")
    List<Racas> obterRacas(String nomeRaca);

    @Query("SELECT * FROM racas WHERE nomeRaca LIKE '%' || :nome || '%'")
    List<Racas> buscarPorNome(String nome);

    @Query("SELECT * FROM racas WHERE usuarioId = :usuarioId ORDER BY nomeRaca")
    List<Racas> obterTodasRacasDoUsuario(int usuarioId);

    @Query("SELECT * FROM racas WHERE id = :id AND usuarioId = :usuarioId LIMIT 1")
    Racas obterRacaDoUsuarioPorId(int usuarioId, int id);

    @Query("SELECT * FROM racas WHERE usuarioId = :usuarioId AND " +
            "(nomeRaca LIKE '%' || :texto || '%' OR tipo LIKE '%' || :texto || '%' OR habitat LIKE '%' || :texto || '%') " +
            "ORDER BY nomeRaca")
    List<Racas> buscarPorNomeDoUsuario(int usuarioId, String texto);

    @Delete
    void deletar(Racas racas);
}
