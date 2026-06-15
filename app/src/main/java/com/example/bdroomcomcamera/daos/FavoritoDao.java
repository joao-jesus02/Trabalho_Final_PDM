package com.example.bdroomcomcamera.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bdroomcomcamera.entities.Favorito;
import com.example.bdroomcomcamera.entities.Racas;

import java.util.List;

@Dao
public interface FavoritoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void favoritar(Favorito favorito);

    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND racaId = :racaId")
    void desfavoritar(int usuarioId, int racaId);

    @Query("SELECT COUNT(*) > 0 FROM favoritos WHERE usuarioId = :usuarioId AND racaId = :racaId")
    boolean estaFavoritada(int usuarioId, int racaId);

    @Query("SELECT r.* FROM racas r INNER JOIN favoritos f ON r.id = f.racaId " +
            "WHERE f.usuarioId = :usuarioId ORDER BY f.dataFavoritado DESC")
    List<Racas> obterFavoritasDoUsuario(int usuarioId);
}
