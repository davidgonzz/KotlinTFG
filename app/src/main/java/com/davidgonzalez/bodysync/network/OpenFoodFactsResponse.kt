package com.davidgonzalez.bodysync.network

import com.google.gson.annotations.SerializedName

data class OpenFoodFactsResponse(
    val product: Product?
)

data class Product(
    @SerializedName("product_name") val product_name: String?,
    val nutriments: Nutriments?
)

data class Nutriments(
    @SerializedName("energy-kcal_100g") val kcalPer100g: Double?
)
