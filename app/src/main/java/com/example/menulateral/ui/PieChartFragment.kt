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
import com.example.menulateral.model.CubeAgente
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PiChartFragment : Fragment() {

    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PiChartFragment", "onCreateView: Creating PieChart fragment")
        return inflater.inflate(R.layout.fragment_piechart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart = view.findViewById(R.id.pieChart)
        Log.d("PiChartFragment", "onViewCreated: View created for PieChart")
        fetchData()
    }

    private fun fetchData() {
        Log.d("PiChartFragment", "fetchData: Fetching data from API")
        RetrofitInstance.api.getPieData().enqueue(object : Callback<List<CubeAgente>> {
            override fun onResponse(call: Call<List<CubeAgente>>, response: Response<List<CubeAgente>>) {
                if (response.isSuccessful) {
                    response.body()?.let { agentesList ->
                        Log.d("PiChartFragment", "Data fetched successfully, count: ${agentesList.size}")
                        setupChart(agentesList)
                    } ?: run {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CubeAgente>>, t: Throwable) {
                Log.e("API Failure", "fetchData: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupChart(agentesList: List<CubeAgente>) {
        Log.d("PieChartFragment", "setupChart: Configurando el gráfico de pastel")

        // Verificamos si hay agentes con datos válidos y convertimos el totalVenta de Double a Float
        val entries = agentesList.mapNotNull { agente ->
            val nombre = agente.nombre ?: return@mapNotNull null // Si el nombre es nulo, ignoramos la entrada
            val totalVenta = agente.totalVenta.toFloat() // Convertimos totalVenta a Float

            // Crea la entrada de PieEntry
            PieEntry(totalVenta, nombre)
        }

        // Verificamos que haya al menos una entrada válida
        if (entries.isEmpty()) {
            Log.e("PieChartFragment", "setupChart: No se encontraron entradas válidas para el gráfico")
            return
        }

        // Creamos el conjunto de datos para el gráfico de pastel
        val dataSet = PieDataSet(entries, "Top 5 Agentes").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.lila2),
                ContextCompat.getColor(requireContext(), R.color.cyan),
                ContextCompat.getColor(requireContext(), R.color.blue),
                ContextCompat.getColor(requireContext(), R.color.pink),
                ContextCompat.getColor(requireContext(), R.color.green2)
            )
            valueTextColor = Color.BLACK
            sliceSpace = 2f // Espacio entre cada porción del gráfico
            setDrawValues(true) // Mostrar los valores en el gráfico
        }

        // Creamos el objeto PieData para el gráfico
        val pieData = PieData(dataSet)

        // Configuramos el gráfico
        pieChart.data = pieData
        pieChart.setUsePercentValues(true) // Mostrar valores en porcentaje
        pieChart.description.isEnabled = false // Deshabilitamos la descripción del gráfico
        pieChart.isRotationEnabled = true // Permitimos la rotación manual del gráfico
        pieChart.animateY(1000) // Animación al mostrar el gráfico
        pieChart.invalidate() // Refresca el gráfico

        Log.d("PieChartFragment", "setupChart: Configuración del gráfico de pastel completada")
    }

}