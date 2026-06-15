package com.example.bdroomcomcamera.database;

import android.content.Context;

import androidx.room.Room;

public final class DatabaseProvider {

    private static volatile AppDatabase database;

    private DatabaseProvider() {
    }

    public static AppDatabase getDatabase(Context context) {
        if (database == null) {
            synchronized (DatabaseProvider.class) {
                if (database == null) {
                    database = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "meu_banco.db"
                            )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return database;
    }
}
