package com.rk.keyboardplayground.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.rk.keyboardplayground.model.Pair;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface DictionaryDao {

    @Query("SELECT * FROM pairs")
    Observable<List<Pair>> getDictionary();

    @Query("UPDATE pairs SET frequency=frequency+1 WHERE name=:s ")
    Completable addFrequency(String s);
}
