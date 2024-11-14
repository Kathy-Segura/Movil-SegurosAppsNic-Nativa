package com.example.menulateral.model

import com.google.gson.annotations.SerializedName

data class CubeMes(
    @SerializedName("Month Name") val monthName: String,
    @SerializedName("Total Venta") val totalVenta: Float
)