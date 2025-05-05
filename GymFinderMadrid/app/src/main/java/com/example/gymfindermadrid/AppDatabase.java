package com.example.gymfindermadrid;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {GimnasioEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract GimnasioDao gimnasioDao();
}
