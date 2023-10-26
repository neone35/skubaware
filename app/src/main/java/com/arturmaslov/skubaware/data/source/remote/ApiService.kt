package com.arturmaslov.skubaware.data.source.remote

import com.arturmaslov.skubaware.data.models.Product
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("query_results-2023-10-24_20438.json")
    fun fetchProductResponse(): Call<List<Product>>

}