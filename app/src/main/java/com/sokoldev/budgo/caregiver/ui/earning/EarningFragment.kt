package com.sokoldev.budgo.caregiver.ui.earning

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.sokoldev.budgo.common.data.models.response.Data
import com.sokoldev.budgo.common.data.models.response.Earning
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.FragmentEarningBinding

class EarningFragment : Fragment() {

    private var _binding: FragmentEarningBinding? = null
    private lateinit var barChart: BarChart
    private lateinit var dataSet: BarDataSet
    private val originalColors = listOf(Color.DKGRAY)
    private val highlightedColor = Color.GREEN
    private val viewModel: EarningViewModel by viewModels()
    private lateinit var helper: PreferenceHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentEarningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        helper = PreferenceHelper.getPref(requireContext())
        barChart = binding.barchart
        helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)?.let {
            viewModel.getDriverEarnings(it)
        }
        initObserver()
        setupBarChart()
        return root
    }

    private fun initObserver() {
        viewModel.driverEarning.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Success -> {
                    val data = response.data.data
                    val earnings = data.earnings
                    showData(data)
                    loadChartData(earnings)

                }

                is ApiResponse.Error -> {
                    val errorMessage = response.errorMessage
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                }

                is ApiResponse.Loading -> {

                }


            }

        }
    }

    private fun setupBarChart() {

        barChart.apply {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // Set up x-axis
            val xAxis = barChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                private val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                override fun getFormattedValue(value: Float): String {
                    return days.getOrElse(value.toInt()) { "" }
                }
            }

            // Add value selected listener
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        val index = dataSet.getEntryIndex(e)
                        dataSet.colors = originalColors.mapIndexed { i, color ->
                            if (i == index) highlightedColor else color
                        }
                        barChart.invalidate() // Refresh the chart
                        Snackbar.make(barChart, "Selected earnings: ${it.y}", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onNothingSelected() {
                    // Do nothing
                }
            })
        }
    }

    private fun loadChartData(earningsList: List<Earning>) {
        val entries = mutableListOf<BarEntry>()
        val dayLabels = mutableListOf<String>()

        for ((index, item) in earningsList.withIndex()) {
            entries.add(BarEntry(index.toFloat(), item.totalEarnings.toFloat()))
            dayLabels.add(item.day)
        }

        dataSet = BarDataSet(entries, "Weekly Earnings")
        dataSet.colors = originalColors
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        barChart.data = data

        // Update x-axis labels
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)

        barChart.invalidate()
    }

    fun showData(data: Data) {
        binding.todayDate.text = data.todayDate
        binding.todayEarnings.text = data.todayEarnings
        binding.acceptanceRate.text = data.acceptanceRate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}