package com.arturmaslov.skubaware.data.source.local

import androidx.room.TypeConverter
import com.arturmaslov.skubaware.data.models.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()

    @JvmStatic
    @TypeConverter
    fun jsonToProductList(json: String?): List<Product>? {
        if (json == null) {
            return null
        }
        val listType = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(json, listType)
    }

    @JvmStatic
    @TypeConverter
    fun productListToJson(products: List<Product>?): String {
        return gson.toJson(products)
    }

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