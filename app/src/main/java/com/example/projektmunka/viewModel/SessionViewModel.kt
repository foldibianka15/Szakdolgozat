package com.example.projektmunka.viewModel

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.example.firstapp.repository.UserDataRepository
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.repository.AuthRepository
import com.example.projektmunka.repository.UserRouteRepository
import com.example.projektmunka.repository.UserRouteTrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userRouteTracker: UserRouteTracker,
    private val userDataRepository: UserDataRepository,
    private val userRouteTrackerRepository: UserRouteTrackerRepository,
    private val routeRepository: UserRouteRepository
) : BaseViewModel() {

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation = _lastLocation.asStateFlow()

    private val _distanceTravelled = MutableStateFlow(0.0)
    val distanceTravelled = _distanceTravelled.asStateFlow()

    private val _calorieBurned = MutableStateFlow(0.0)
    val calorieBurned = _calorieBurned.asStateFlow()

    private val _averageSpeed = MutableStateFlow(0.0)
    val averageSpeed = _averageSpeed.asStateFlow()

    private val _stepsTaken = MutableStateFlow(0)
    val stepsTaken = _stepsTaken.asStateFlow()

    private val _duration = MutableStateFlow(0L) //
    val duration = _duration.asStateFlow()

    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _averageHeartRate = MutableStateFlow(0.0)
    val averageHeartRate = _averageHeartRate.asStateFlow()

    private val _generatedRoute = MutableStateFlow<Route?>(null)
    val generatedRoute = _generatedRoute.asStateFlow()

    private val _startTime = MutableStateFlow<Long?>(null)
    val startTime = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow<Long?>(null)
    val endTime = _endTime.asStateFlow()

    private val _isSessionFinished = MutableStateFlow(false)
    val isSessionFinished = _isSessionFinished.asStateFlow()

    init {
        viewModelScope.launch {
            userRouteTracker.lastLocation.collect { location ->
                _lastLocation.value = location
            }
        }
        viewModelScope.launch {
            userRouteTracker.distanceTravelled.collect { distance ->
                _distanceTravelled.value = distance
            }
        }
        viewModelScope.launch {
            userRouteTracker.calorieBurned.collect { calorie ->
                _calorieBurned.value = calorie
            }
        }
        viewModelScope.launch {
            userRouteTracker.averageSpeed.collect { speed ->
                _averageSpeed.value = speed
            }
        }
        viewModelScope.launch {
            userRouteTracker.stepsTaken.collect { steps ->
                _stepsTaken.value = steps
            }
        }
        viewModelScope.launch {
            userRouteTracker.heartRate.collect { heartRate ->
                _heartRate.value = heartRate
            }
        }
        viewModelScope.launch {
            userRouteTracker.averageHeartRate.collect { averageHeartRate ->
                _averageHeartRate.value = averageHeartRate
            }
        }
        viewModelScope.launch {
            userRouteTracker.startTime.collect { startTime ->
                _startTime.value = startTime
            }
        }
        viewModelScope.launch {
            userRouteTracker.endTime.collect { endTime ->
                _endTime.value = endTime
            }
        }
        viewModelScope.launch {
            userRouteTracker.duration.collect { duration ->
                _duration.value = duration
            }
        }
        viewModelScope.launch {
            userRouteTracker.isSessionFinished.collect { isFinished ->
                _isSessionFinished.value = isFinished
            }
        }
        viewModelScope.launch {
            userRouteTracker.generatedRoute.collect { route ->
                _generatedRoute.value = route
            }
        }
    }

    fun startSession() {
        userRouteTracker.startSession()
    }

    fun resumeSession() {
    }

    fun stopSession() {
        viewModelScope.launch {
            userDataRepository.currentUserData.collect { user ->
                val userId = user?.id

                println("userId: $userId")

                val userRouteTracker = userRouteTrackerRepository.saveSessionDataIntoFirestore(
                    startTime.value,
                    endTime.value,
                    duration.value,
                    25,
                    calorieBurned.value,
                    averageSpeed.value,
                    98.0,
                    isSessionFinished.value
                )

                println("userRouteTracker: ${userRouteTracker.averageHeartRate}")

                generatedRoute.value?.let { route ->
                    if (userId != null) {
                        println("Kerék")
                        routeRepository.saveUserRouteIntoFirestore(route, userId, userRouteTracker.id)
                    }
                }
            }
        }
    }
}