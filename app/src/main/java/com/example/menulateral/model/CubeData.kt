package com.example.menulateral.model

import com.google.gson.annotations.SerializedName

/*data class CubeData(
    val Nombre: String,
    @SerializedName("Monto Comision") val Monto_Comision: Float, // Asegúrate de que este tipo sea correcto
    val Precio: Float
)*/

data class CubeData(
    val Nombre: String,
    @SerializedName("Monto Comision Total") val Monto_Comision: Float,
    @SerializedName("Precio  Total") val Precio: Float
)
