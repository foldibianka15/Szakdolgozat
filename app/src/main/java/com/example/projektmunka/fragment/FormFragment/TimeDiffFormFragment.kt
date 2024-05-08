package com.example.projektmunka.fragment.FormFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.projektmunka.SessionActivity
import com.example.projektmunka.databinding.FragmentForm3Binding
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.viewModel.routeGeneratorViewmodel.TimeDiffRouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimeDiffFormFragment : Fragment() {

    private lateinit var binding: FragmentForm3Binding
    private val timeDiffRouteViewModel: TimeDiffRouteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForm3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreateRoute = binding.btnCreateRoute

        btnCreateRoute.setOnClickListener {
            val selectedRadioButtonId = binding.radioGroupLocation.checkedRadioButtonId
            val selectedRadioButton = binding.root.findViewById<RadioButton>(selectedRadioButtonId)
            val address = binding.editTextAddress.text.toString()
            val targetAddress = binding.editTextTargetLocation.text.toString()
            val time = binding.editTextTime.toString().toDoubleOrNull() ?: 0.0

            if (selectedRadioButton != null) {
                val selectedOptionText = selectedRadioButton.text.toString()

                if (selectedOptionText == "Choose Address") {
                    lifecycleScope.launch {
                        timeDiffRouteViewModel.startSession(RouteGeneratorType.TIMEDIFFGENERATOR, address, targetAddress, time)
                    }
                } else {
                    lifecycleScope.launch {
                        timeDiffRouteViewModel.startSession(RouteGeneratorType.TIMEDIFFGENERATOR, "", targetAddress, time)
                    }
                }
            }
            (activity as? SessionActivity)?.showDataFragment()
        }
    }
}
