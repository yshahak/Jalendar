package com.thedroidboy.jalendar.model;

import android.arch.persistence.room.TypeConverter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by $Yaakov Shahak on 12/29/2017.
 */

public class Converters {

    private static Moshi moshi = new Moshi.Builder().build();
    private static JsonAdapter<List<Day>> adapter;
    static {
        Type type = Types.newParameterizedType(List.class, Day.class);
        adapter = moshi.adapter(type);
    }

    @TypeConverter
    public static List<Day> fromString(String value) {
        try {
            return adapter.fromJson(value);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @TypeConverter
    public static String fromArrayList(List<Day> list) {
        return adapter.toJson(list);
    }
}