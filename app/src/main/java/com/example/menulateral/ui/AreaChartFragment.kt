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
import com.example.menulateral.model.CubeMes
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AreChartFragment : Fragment() {

    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("AreChartFragment", "onCreateView: Creating AreaChart fragment")
        return inflater.inflate(R.layout.fragment_areachart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart = view.findViewById(R.id.areaChart)
        Log.d("AreChartFragment", "onViewCreated: View created for AreaChart")
        fetchData()
    }

    private fun fetchData() {
        Log.d("AreaChartFragment", "fetchData: Fetching data from API")
        RetrofitInstance.api.getAreaData().enqueue(object : Callback<List<CubeMes>> {
            override fun onResponse(call: Call<List<CubeMes>>, response: Response<List<CubeMes>>) {
                if (response.isSuccessful) {
                    response.body()?.let { mesList ->
                        Log.d("AreaChartFragment", "Data fetched successfully, count: ${mesList.size}")
                        setupChart(mesList)
                    } ?: run {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CubeMes>>, t: Throwable) {
                Log.e("API Failure", "fetchData: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupChart(mesList: List<CubeMes>) {
        Log.d("AreaChartFragment", "setupChart: Setting up the chart")

        val entries = mesList.mapIndexed { index, mes ->
            Entry(index.toFloat(), mes.totalVenta)
        }

        val dataSet = LineDataSet(entries, "Total Venta por Mes").apply {
            color = ContextCompat.getColor(requireContext(), R.color.lila2)
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircles(true)
            setDrawValues(true)
            setDrawFilled(true) // Para llenar el área
            fillColor = ContextCompat.getColor(requireContext(), R.color.lila2) // Color del área
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configuración del gráfico
        lineChart.axisLeft.axisMinimum = 0f // Mínimo en el eje Y
        lineChart.xAxis.labelRotationAngle = -45f // Rotar etiquetas del eje X

        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return mesList.getOrNull(value.toInt())?.monthName ?: ""
            }
        }

        // Animación al cargar el gráfico
        lineChart.animateY(1500) // Animación vertical de 1500ms (1.5 segundos)

        lineChart.invalidate() // Refresca el gráfico
        Log.d("AreaChartFragment", "setupChart: Chart setup complete")
    }
}