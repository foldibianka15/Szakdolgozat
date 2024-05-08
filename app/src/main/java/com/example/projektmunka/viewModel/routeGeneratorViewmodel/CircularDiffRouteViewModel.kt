package com.example.projektmunka.viewModel.routeGeneratorViewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.RouteGenerator.CircularDiffRouteGenerator
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CircularDiffRouteViewModel @Inject constructor(private val userRouteTracker: UserRouteTracker, private val circularDiffRouteGenerator: CircularDiffRouteGenerator): BaseViewModel() {

    private val _generatedRoute = MutableStateFlow<Route?>(null)
    val generatedRoute = _generatedRoute.asStateFlow()

    private val _generatedRoutePOIs = MutableStateFlow<Route?>(null)
    val generatedRoutePois = _generatedRoutePOIs.asStateFlow()

    init {
        viewModelScope.launch {
            userRouteTracker.generatedRoute.collect { route ->
                _generatedRoute.value = route
            }
        }
        viewModelScope.launch {
            userRouteTracker.generatedRoutePois.collect { route ->
                _generatedRoutePOIs.value = route
            }
        }
    }

    suspend fun startSession(
        generatorType: RouteGeneratorType,
        address: String,
        maxWalkingTimeInHours: Double
    ) {
        when (generatorType) {
            RouteGeneratorType.CIRCULARDIFFGENERATOR -> {
                circularDiffRouteGenerator.startTrackingCircularDiff(address, maxWalkingTimeInHours)
            }
            else -> {
                Log.e(ContentValues.TAG, "Unsupported route generator type: $generatorType")
            }
        }
    }
}