package com.example.menulateral.api

import com.example.menulateral.model.CubeAgente
import com.example.menulateral.model.CubeCliente
import com.example.menulateral.model.CubeData
import com.example.menulateral.model.CubeMes
import com.example.menulateral.model.CubeVenta

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/apiolap/cubedata")
    fun getBartData(): Call<List<CubeData>>

    @GET("/apiolap/cubeventa")
    fun getLineData(): Call<List<CubeVenta>>

    @GET("/apiolap/cubecliente")
    fun getColumnData(): Call<List<CubeCliente>>

    @GET("/apiolap/cubemes")
    fun getAreaData(): Call<List<CubeMes>>

    @GET("/apiolap/cubeagente")
    fun getPieData(): Call<List<CubeAgente>>

}
