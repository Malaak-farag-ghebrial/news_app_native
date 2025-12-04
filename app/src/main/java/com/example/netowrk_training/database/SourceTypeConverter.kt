package com.example.netowrk_training.database

import androidx.room.TypeConverter
import com.example.netowrk_training.models.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SourceTypeConverter {

    @TypeConverter
    fun fromSource(source: Source): String{
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(json: String) : Source{
        val type = object : TypeToken<Source>() {}.type
        return Gson().fromJson<Source>(json,type)
    }


}