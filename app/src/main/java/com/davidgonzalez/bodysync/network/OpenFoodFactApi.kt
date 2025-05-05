package com.davidgonzalez.bodysync.network

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v0/product/{code}.json")
    suspend fun getProduct(@Path("code") code: String): OpenFoodFactsResponse
}
