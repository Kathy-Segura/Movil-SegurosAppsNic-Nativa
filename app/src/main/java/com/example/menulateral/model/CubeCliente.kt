package com.example.menulateral.model

import com.google.gson.annotations.SerializedName

data class CubeCliente(
    val Nombre: String,
    @SerializedName("Total Ventas") val totalVenta: Double
)
