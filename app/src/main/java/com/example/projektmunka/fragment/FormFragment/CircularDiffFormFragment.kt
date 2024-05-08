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
import com.example.projektmunka.databinding.FragmentForm1Binding
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.viewModel.routeGeneratorViewmodel.CircularDiffRouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CircularDiffFormFragment() : Fragment() {

    private lateinit var binding: FragmentForm1Binding
    private val circularDiffRouteViewModel: CircularDiffRouteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForm1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreateRoute = binding.btnCreateRoute

        btnCreateRoute.setOnClickListener {
            val selectedRadioButtonId = binding.radioGroupLocation.checkedRadioButtonId
            val selectedRadioButton = binding.root.findViewById<RadioButton>(selectedRadioButtonId)
            val address = binding.editTextAddress.text.toString()
            val maxWalkingTimeInHours = binding.editTextMaxWalkingTime.text.toString().toDoubleOrNull() ?: 0.0

            if (selectedRadioButton != null) {
                val selectedOptionText = selectedRadioButton.text.toString()

                if (selectedOptionText == "Choose Address") {
                    lifecycleScope.launch {
                        circularDiffRouteViewModel.startSession(RouteGeneratorType.CIRCULARDIFFGENERATOR, address, maxWalkingTimeInHours)
                    }
                } else  {
                    lifecycleScope.launch {
                        circularDiffRouteViewModel.startSession(RouteGeneratorType.CIRCULARDIFFGENERATOR, "", maxWalkingTimeInHours)
                    }
                }
            }
            (activity as? SessionActivity)?.showDataFragment()
        }
    }
}