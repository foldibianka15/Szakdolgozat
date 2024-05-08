package com.example.projektmunka.viewModel.routeGeneratorViewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.RouteGenerator.CaloriesDiffRouteGenerator
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaloriesDiffRouteViewModel@Inject constructor(private val userRouteTracker: UserRouteTracker, private val caloriesDiffRouteGenerator: CaloriesDiffRouteGenerator): BaseViewModel()  {
    suspend fun startSession(generatorType: RouteGeneratorType, source: String, destination: String, calories: Double) {
        when(generatorType) {
            RouteGeneratorType.CALORIESDIFFGENERATOR -> {
                caloriesDiffRouteGenerator.startTrackingCaloriesDiff(source, destination, calories)
            }
            else -> {
                Log.e(ContentValues.TAG, "Unsupported route generator type: $generatorType")
            }
        }
    }

}