package com.example.bdroomcomcamera.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Representa a ação de um usuário favoritar uma criatura.
 * A chave composta impede que a mesma criatura seja favoritada duas vezes.
 */
@Entity(
        tableName = "favoritos",
        primaryKeys = {"usuarioId", "racaId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Usuario.class,
                        parentColumns = "id",
                        childColumns = "usuarioId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Racas.class,
                        parentColumns = "id",
                        childColumns = "racaId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("usuarioId"), @Index("racaId")}
)
public class Favorito {
    private int usuarioId;
    private int racaId;
    private long dataFavoritado;

    public Favorito(int usuarioId, int racaId, long dataFavoritado) {
        this.usuarioId = usuarioId;
        this.racaId = racaId;
        this.dataFavoritado = dataFavoritado;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getRacaId() {
        return racaId;
    }

    public void setRacaId(int racaId) {
        this.racaId = racaId;
    }

    public long getDataFavoritado() {
        return dataFavoritado;
    }

    public void setDataFavoritado(long dataFavoritado) {
        this.dataFavoritado = dataFavoritado;
    }
}
