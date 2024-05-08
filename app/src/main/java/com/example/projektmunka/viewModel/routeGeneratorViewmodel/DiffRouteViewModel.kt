package com.example.projektmunka.viewModel.routeGeneratorViewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.RouteGenerator.DiffRouteGenerator
import com.example.projektmunka.logic.RouteGenerator.RouteGeneratorType
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiffRouteViewModel @Inject constructor(private val userRouteTracker: UserRouteTracker, private val diffRouteGenerator: DiffRouteGenerator): BaseViewModel() {

    suspend fun startSession(generatorType: RouteGeneratorType, source: String, destination: String) {
        when(generatorType) {
            RouteGeneratorType.DIFFGENERATOR -> {
               diffRouteGenerator.startTrackingDiff(source, destination)
            }
            else -> {
                Log.e(ContentValues.TAG, "Unsupported route generator type: $generatorType")
            }
        }
    }
}