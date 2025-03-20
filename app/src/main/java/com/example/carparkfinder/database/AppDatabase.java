package com.example.carparkfinder.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.carparkfinder.model.CarParkEntity;

/*
Creates a singleton Room database instance
Provides a way to access CarParkDao
*/

@Database(entities = {CarParkEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract CarParkDao carParkDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "carpark_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

