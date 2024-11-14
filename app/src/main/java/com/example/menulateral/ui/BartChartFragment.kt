package com.example.menulateral.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.menulateral.R
import com.example.menulateral.api.RetrofitInstance
import com.example.menulateral.model.CubeData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarChartFragment : Fragment(R.layout.fragment_barchart) {
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BarChartFragment", "onCreateView: Creating BarChart fragment")
        return inflater.inflate(R.layout.fragment_barchart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChart = view.findViewById(R.id.barChart)
        Log.d("BarChartFragment", "onViewCreated: View created for BarChart")
        fetchData()
    }

    private fun fetchData() {
        Log.d("BarChartFragment", "fetchData: Fetching data from API")
        RetrofitInstance.api.getBartData().enqueue(object : Callback<List<CubeData>> {
            override fun onResponse(call: Call<List<CubeData>>, response: Response<List<CubeData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { cubeDataList ->
                        Log.d("BarChartFragment", "Data fetched successfully, count: ${cubeDataList.size}")
                        Log.d("BarChartFragment", "Fetched data: $cubeDataList") // Registro de datos

                        // Verifica si hay datos con comisiones diferentes de cero
                        val filteredData = cubeDataList.filter { it.Monto_Comision > 0 }
                        if (filteredData.isNotEmpty()) {
                            setupChart(filteredData)
                        } else {
                            Log.e("Data Error", "No data with valid commission values.")
                        }
                    } ?: run {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CubeData>>, t: Throwable) {
                Log.e("API Failure", "fetchData: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupChart(cubeDataList: List<CubeData>) {
        Log.d("BarChartFragment", "setupChart: Setting up the chart")

        // Crear las entradas para el gráfico de barras
        val entries = cubeDataList.mapIndexed { index, cubeData ->
            BarEntry(index.toFloat(), cubeData.Monto_Comision.toFloat())
        }

        // Crear el conjunto de datos para el gráfico
        val dataSet = BarDataSet(entries, "Monto Comisión").apply {
            color = ContextCompat.getColor(requireContext(), R.color.blue)
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.9f // Ajusta el ancho de las barras
        }

        // Asignar los datos al gráfico
        barChart.data = barData

        // Ajustar el gráfico
        barChart.setFitBars(true) // Ajusta las barras al espacio disponible
        barChart.setDrawGridBackground(false) // Desactiva el fondo de cuadrícula
        barChart.axisLeft.setDrawGridLines(true) // Habilita las líneas de la cuadrícula en el eje Y
        barChart.xAxis.setDrawGridLines(true) // Habilita las líneas de la cuadrícula en el eje X

        // Configurar el eje Y
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false // Deshabilitar el eje derecho

        // Configurar el eje X
        val xAxis = barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(true) // Habilita las líneas de la cuadrícula en el eje X
            valueFormatter = IndexAxisValueFormatter(cubeDataList.map { it.Nombre }) // Nombres de agentes en el eje X
            labelRotationAngle = -45f // Rotar etiquetas del eje X
        }

        // Animación al cargar el gráfico
        barChart.animateY(1500) // Animación vertical de 1500ms (1.5 segundos)

        // Refrescar el gráfico
        barChart.invalidate()

        Log.d("BarChartFragment", "setupChart: Chart setup complete")
    }
}