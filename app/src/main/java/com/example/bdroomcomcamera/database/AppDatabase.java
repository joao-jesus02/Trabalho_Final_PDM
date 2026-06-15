package com.example.bdroomcomcamera.database;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.bdroomcomcamera.daos.FavoritoDao;
import com.example.bdroomcomcamera.daos.RacasDao;
import com.example.bdroomcomcamera.daos.UsuarioDao;
import com.example.bdroomcomcamera.entities.Favorito;
import com.example.bdroomcomcamera.entities.Racas;
import com.example.bdroomcomcamera.entities.Usuario;

@Database(entities = {Usuario.class, Racas.class, Favorito.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract RacasDao racasDao();
    public abstract FavoritoDao favoritoDao();
}
