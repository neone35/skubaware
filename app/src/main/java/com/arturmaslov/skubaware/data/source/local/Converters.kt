package com.arturmaslov.tgnba.data.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()

    @JvmStatic
    @TypeConverter
    fun listToString(list: List<String?>?): String {
        return gson.toJson(list)
    }

    @JvmStatic
    @TypeConverter
    fun stringToList(data: String?): List<String>? {
        if (data == null) {
            return null
        }
        val listType = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(data, listType)
    }

}