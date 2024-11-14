package com.example.menulateral.model

import com.google.gson.annotations.SerializedName

data class CubeAgente(
    @SerializedName("Nombre") val nombre: String?,  // Permitimos que sea nulo para evitar problemas
    @SerializedName("Total Venta") val totalVenta: Double
)