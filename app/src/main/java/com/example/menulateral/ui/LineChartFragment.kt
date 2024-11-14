package com.example.menulateral.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.menulateral.api.RetrofitInstance
import com.example.menulateral.model.CubeVenta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat
import android.graphics.Color
import com.example.menulateral.R
import com.github.mikephil.charting.components.XAxis


class LinChartFragment : Fragment(R.layout.fragment_linechart) {
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LinChartFragment", "onCreateView: Creating LinChart fragment")
        return inflater.inflate(R.layout.fragment_linechart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart = view.findViewById(R.id.lineChart)
        Log.d("LinChartFragment", "onViewCreated: View created for LinChart")
        fetchData()
    }

    private fun fetchData() {
        Log.d("LinChartFragment", "fetchData: Fetching data from API")
        RetrofitInstance.api.getLineData().enqueue(object : Callback<List<CubeVenta>> {
            override fun onResponse(call: Call<List<CubeVenta>>, response: Response<List<CubeVenta>>) {
                if (response.isSuccessful) {
                    response.body()?.let { cubeVentaList ->
                        Log.d("LinChartFragment", "Data fetched successfully, count: ${cubeVentaList.size}")
                        setupChart(cubeVentaList)
                    } ?: run {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CubeVenta>>, t: Throwable) {
                Log.e("API Failure", "fetchData: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupChart(cubeVentaList: List<CubeVenta>) {
        Log.d("LinChartFragment", "setupChart: Setting up the chart")

        // Crea las entradas para el gráfico de líneas
        val entries = cubeVentaList.mapIndexed { index, poliza ->
            Entry(index.toFloat(), poliza.totalVenta)
        }

        val dataSet = LineDataSet(entries, "Total Venta").apply {
            color = ContextCompat.getColor(requireContext(), R.color.cyan) // Color de la línea
            valueTextColor = Color.BLACK // Color del texto de los valores
            valueTextSize = 12f // Tamaño del texto de los valores
            lineWidth = 2.5f // Grosor de la línea
            circleRadius = 6f // Radio de los puntos en la línea
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.cyan)) // Color de los puntos
            setDrawCircleHole(true) // Habilitar el agujero en el círculo
            circleHoleColor = Color.WHITE // Color del agujero del círculo
            mode = LineDataSet.Mode.CUBIC_BEZIER // Estilo de línea suave
            highLightColor = Color.RED // Color de resaltado al seleccionar un punto
            setDrawFilled(true) // Habilita el área de llenado
            fillColor = ContextCompat.getColor(requireContext(), R.color.green) // Color de llenado
            fillAlpha = 100 // Transparencia del área de llenado
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configura el gráfico
        lineChart.description.isEnabled = false // Desactiva la descripción del gráfico
        lineChart.animateXY(2000, 2000) // Animación más suave y prolongada

        // Configura el eje Y
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisRight.isEnabled = false // Deshabilitar el eje derecho
        lineChart.axisLeft.setDrawGridLines(true) // Habilita las líneas de la cuadrícula en el eje Y
        lineChart.axisLeft.gridColor = Color.LTGRAY // Cambia el color de la cuadrícula

        // Configura el eje X
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(true) // Habilita las líneas de la cuadrícula en el eje X
        xAxis.gridColor = Color.LTGRAY // Cambia el color de la cuadrícula

        // Asigna los nombres de las pólizas al eje X
        xAxis.valueFormatter = IndexAxisValueFormatter(cubeVentaList.map { it.nombrePoliza })
        xAxis.labelRotationAngle = -45f // Rotar etiquetas del eje X para mejor visualización

        lineChart.invalidate() // Refresca el gráfico
        Log.d("LinChartFragment", "setupChart: Chart setup complete")
    }
}