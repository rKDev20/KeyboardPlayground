package com.rk.keyboardplayground.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rk.keyboardplayground.database.dao.DictionaryDao;
import com.rk.keyboardplayground.model.Pair;

@Database(entities = Pair.class, version = 1, exportSchema = false)
public abstract class DatabaseHelper extends RoomDatabase {
    private final static String DB_NAME = "dictionary";

    public abstract DictionaryDao dao();

    private static DatabaseHelper INSTANCE;

    public static DatabaseHelper getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(context, DatabaseHelper.class, DB_NAME)
                    .createFromAsset("dictionary.db")
                    .build();
        return INSTANCE;
    }
}
