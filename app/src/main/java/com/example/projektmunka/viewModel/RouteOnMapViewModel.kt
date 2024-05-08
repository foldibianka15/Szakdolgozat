package com.example.projektmunka.viewModel

import androidx.lifecycle.viewModelScope
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.UserRouteTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteOnMapViewModel@Inject constructor(private val userRouteTracker: UserRouteTracker): BaseViewModel() {

    private val _generatedRoute = MutableStateFlow<Route?>(null)
    val generatedRoute = _generatedRoute.asStateFlow()

    init {
        viewModelScope.launch {
            userRouteTracker.generatedRoute.collect { route ->
                _generatedRoute.value = route
            }
        }
    }
}