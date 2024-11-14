package com.example.menulateral.model

import com.google.gson.annotations.SerializedName

data class CubeVenta(
    @SerializedName("Nombre Poliza") val nombrePoliza: String,
    @SerializedName("Total Venta") val totalVenta: Float
)
