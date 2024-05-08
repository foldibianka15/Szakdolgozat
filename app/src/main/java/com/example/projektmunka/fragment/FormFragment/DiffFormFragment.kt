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
import com.example.projektmunka.databinding.FragmentForm2Binding
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.viewModel.routeGeneratorViewmodel.DiffRouteViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiffFormFragment() : Fragment() {

    private lateinit var binding: FragmentForm2Binding
    private val diffRouteViewModel: DiffRouteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForm2Binding.inflate(inflater, container, false)
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

            if (selectedRadioButton != null) {
                val selectedOptionText = selectedRadioButton.text.toString()

                if (selectedOptionText == "Choose Address") {
                    lifecycleScope.launch {
                        diffRouteViewModel.startSession(RouteGeneratorType.DIFFGENERATOR, address, targetAddress)
                    }
                } else {
                    lifecycleScope.launch {
                        diffRouteViewModel.startSession(RouteGeneratorType.DIFFGENERATOR, "", targetAddress)
                    }
                }
            }
            (activity as? SessionActivity)?.showDataFragment()
        }
    }
}
