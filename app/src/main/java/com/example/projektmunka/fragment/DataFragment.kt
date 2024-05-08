package com.example.projektmunka.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.projektmunka.SessionActivity
import com.example.projektmunka.databinding.FragmentDataBinding
import com.example.projektmunka.viewModel.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DataFragment : Fragment() {

    private lateinit var binding: FragmentDataBinding
    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton = binding.startButton
        val resumeButton = binding.resumeButton
        val stopButton = binding.stopButton

        startButton.setOnClickListener {
            lifecycleScope.launch {
                startButton.visibility = View.GONE
                resumeButton.visibility = View.VISIBLE
                stopButton.visibility = View.VISIBLE
                sessionViewModel.startSession()
            }
        }

        resumeButton.setOnClickListener {
            lifecycleScope.launch {
                sessionViewModel.resumeSession()
            }
        }

        stopButton.setOnClickListener {
            lifecycleScope.launch {
                startButton.visibility = View.VISIBLE
                resumeButton.visibility = View.GONE
                stopButton.visibility = View.GONE
                sessionViewModel.stopSession()
            }
            //(activity as? SessionActivity)?.showReportFragment()
        }


        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.distanceTravelled.collect { distance ->
                val displayValue: String = "%.2f".format(distance)
                binding.progressBarDistance.text = displayValue
                binding.progressBarDistance.progress = displayValue.toFloat()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.calorieBurned.collect { calorie ->
                binding.progressCalories.text = "$calorie"
                binding.progressCalories.progress = calorie.toFloat()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.averageSpeed.collect { speed->
                val displayValue: String = "%.2f".format(speed)
                binding.progressBarAverageSpeed.text = displayValue
                binding.progressBarAverageSpeed.progress = displayValue.toFloat()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.stepsTaken.collect { steps ->
                binding.progressBarSteps.text = "$steps"
                binding.progressBarSteps.progress = steps.toFloat()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.heartRate.collect{ heartRate ->
                binding.progressBarAverageHeartRate.text = "$heartRate"
                binding.progressBarHeartRate.progress = heartRate.toFloat()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sessionViewModel.averageHeartRate.collect { averageHeartRate ->
                binding.progressBarAverageHeartRate.text = "$averageHeartRate"
                binding.progressBarAverageHeartRate.progress = averageHeartRate.toFloat()
            }
        }
    }
}