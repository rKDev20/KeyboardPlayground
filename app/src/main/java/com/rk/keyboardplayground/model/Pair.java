package com.rk.keyboardplayground.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pairs")
public class Pair {
    @NonNull
    @PrimaryKey
    public String name;
    public int frequency;
}
