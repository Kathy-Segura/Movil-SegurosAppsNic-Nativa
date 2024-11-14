package com.example.menulateral.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.menulateral.R
import com.example.menulateral.api.ApiService
import com.example.menulateral.api.RetrofitInstance
import com.example.menulateral.model.CubeCliente
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ColumChartFragment : Fragment(R.layout.fragment_columchart) {
    private lateinit var barChart: BarChart

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            Log.d("ColumChartFragment", "onCreateView: Creating ColumnChart fragment")
            return inflater.inflate(R.layout.fragment_columchart, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            barChart = view.findViewById(R.id.columnChart)
            Log.d("ColumChartFragment", "onViewCreated: View created for ColumnChart")
            fetchData()
        }

    private fun fetchData() {
        Log.d("ColumChartFragment", "fetchData: Fetching data from API")
        RetrofitInstance.api.getColumnData().enqueue(object : Callback<List<CubeCliente>> {
            override fun onResponse(call: Call<List<CubeCliente>>, response: Response<List<CubeCliente>>) {
                if (response.isSuccessful) {
                    response.body()?.let { ClientesList ->
                        Log.d("ColumChartFragment", "Data fetched successfully, count: ${ClientesList.size}")
                        Log.d("ColumChartFragment", "Fetched data: $ClientesList")

                        // Filtrar datos
                        val filteredData = ClientesList.filter { it.totalVenta > 0 }
                        if (filteredData.isNotEmpty()) {
                            setupChart(filteredData)
                        } else {
                            Log.e("Data Error", "No data with valid total sales values.")
                        }
                    } ?: run {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CubeCliente>>, t: Throwable) {
                Log.e("API Failure", "fetchData: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupChart(ventasClientesList: List<CubeCliente>) {
        Log.d("ColumChartFragment", "setupChart: Setting up the chart")

        // Crear las entradas para el gráfico
        val entries = ventasClientesList.mapIndexed { index, ventaCliente ->
            BarEntry(index.toFloat(), ventaCliente.totalVenta.toFloat())
        }

        val dataSet = BarDataSet(entries, "Total Venta")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.green2)
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        barData.barWidth = 0.4f // Reducimos el ancho de las barras para que se ajusten mejor

        // Configurar el gráfico de barras
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.setDrawGridBackground(false)

        // Configuración del eje Y
        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.granularity = 100f // Ajusta la granularidad del eje Y según el rango de ventas
        barChart.axisRight.isEnabled = false

        // Configuración del eje X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false) // Quita las líneas de cuadrícula para un diseño más limpio
        xAxis.valueFormatter = IndexAxisValueFormatter(ventasClientesList.map { it.Nombre })
        xAxis.labelRotationAngle = -30f // Ajuste a 30 grados para mejor visibilidad si los nombres son largos

        // Animación para suavizar la presentación del gráfico
        barChart.animateY(1200) // Animación vertical de 1200ms (1.2 segundos)

        // Personaliza el estilo del gráfico
        barChart.description.isEnabled = false // Oculta la descripción por defecto
        barChart.legend.isEnabled = true // Mantén la leyenda para que se vea el label "Total Venta"

        // Estilo adicional
        barChart.setNoDataText("No hay datos para mostrar.")
        barChart.isDoubleTapToZoomEnabled = false // Deshabilita el zoom por doble toque
        barChart.setPinchZoom(false) // Deshabilita el zoom con pinzas

        // Refresca el gráfico
        barChart.invalidate()
        Log.d("ColumChartFragment", "setupChart: Chart setup complete")
    }
}