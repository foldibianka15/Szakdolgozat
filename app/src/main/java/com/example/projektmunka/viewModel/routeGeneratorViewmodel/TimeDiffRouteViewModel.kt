package com.example.projektmunka.viewModel.routeGeneratorViewmodel

import android.content.ContentValues
import android.util.Log
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.logic.RouteGenerator.TimeDiffRouteGenerator
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TimeDiffRouteViewModel @Inject constructor(private val userRouteTracker: UserRouteTracker, private val timeDiffRouteGenerator: TimeDiffRouteGenerator) :
    BaseViewModel() {

    suspend fun startSession(
        generatorType: RouteGeneratorType,
        source: String,
        destination: String,
        time: Double
    ) {
        when(generatorType) {
            RouteGeneratorType.TIMEDIFFGENERATOR -> {
                timeDiffRouteGenerator.startTrackingTimeDiff(source, destination, time)
            }
            else -> {
                Log.e(ContentValues.TAG, "Unsupported route generator type: $generatorType")
            }
        }
    }
}