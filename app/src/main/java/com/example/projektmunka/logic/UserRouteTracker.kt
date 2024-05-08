package com.example.projektmunka.logic

import android.annotation.SuppressLint
import android.location.Location
import com.example.projektmunka.data.Route
import com.example.projektmunka.utils.Constants
import com.example.projektmunka.utils.Constants.TIMER_INTERVAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

@SuppressLint("MissingPermission")
class UserRouteTracker(
    private val locationTracker: LocationTracker,
    private val fitnessCalculator: FitnessCalculator,
    private val stepCounter: StepCounter,
) {

    private var isTracking = false
    private var firstLocation: Location? = null
    private var loopsCompleted = 0
    private var timer: Timer? = null
    private var elapsedTime: Long = 0
    private var lastUpdateTime: Long = 0


    private val _locationList = MutableStateFlow(mutableListOf<Location?>())
    val locationList = _locationList.asStateFlow()

    private val _previousLocation = MutableStateFlow<Location?>(null)
    val previousLocation = _previousLocation.asStateFlow()

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation = _lastLocation.asStateFlow()

    private val _distanceTravelled = MutableStateFlow(0.0)
    val distanceTravelled = _distanceTravelled.asStateFlow()

    private val _averageSpeed = MutableStateFlow(0.0)
    val averageSpeed = _averageSpeed.asStateFlow()

    private val _stepsTaken = MutableStateFlow(0)
    val stepsTaken = _stepsTaken.asStateFlow()

    private val _calorieBurned = MutableStateFlow(0.0)
    val calorieBurned = _calorieBurned.asStateFlow()

    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _averageHeartRate = MutableStateFlow(0.0)
    val averageHeartRate = _averageHeartRate.asStateFlow()

    private val _duration = MutableStateFlow(0L) //
    val duration = _duration.asStateFlow()

    private val _isSessionFinished = MutableStateFlow(false)
    val isSessionFinished = _isSessionFinished.asStateFlow()

    private val _generatedRoute = MutableStateFlow<Route?>(null)
    val generatedRoute = _generatedRoute.asStateFlow()

    private val _currentRoute = MutableStateFlow<Route?>(null)
    val currentRoute = _currentRoute.asStateFlow()

    private val _generatedRoutePOIs = MutableStateFlow<Route?>(null)
    val generatedRoutePois = _generatedRoutePOIs.asStateFlow()

    private val _startTime = MutableStateFlow<Long?>(null)
    val startTime = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow<Long?>(null)
    val endTime = _endTime.asStateFlow()



    init {
        observeLocationData()
    }

    suspend fun generateDiffRoute(
        routeGeneratorFunc: suspend () -> Route
    ) {
        if (!isTracking) {
            isTracking = true

            //útvonalgenerálás indítása
            val route = routeGeneratorFunc()
            _generatedRoute.emit(route)
        }
    }

    suspend fun generateCircularDiffRoute(routeGeneratorFunc: suspend () -> Pair<Route, Route>): Pair<Route, Route>? {
        if (!isTracking) {
            isTracking = true

            val route = routeGeneratorFunc()
            _generatedRoute.emit(route.first)
            _generatedRoutePOIs.emit(route.second)
            println("first route point: ${route.first.path.first()}")
            println("last route point: ${route.first.path.last()}")

            return route
        }
        return null
    }

    fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                elapsedTime += TIMER_INTERVAL
            }

        }, 0, TIMER_INTERVAL.toLong())
    }

    fun startTimer2() {
        lastUpdateTime = System.currentTimeMillis()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                elapsedTime += currentTime - lastUpdateTime
                lastUpdateTime = currentTime
            }
        }, 0, TIMER_INTERVAL.toLong())
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    fun startSession() {

        stepCounter.registerStepCounterListener()
        _startTime.value = System.currentTimeMillis()
        startTimer2()
    }

    fun stopSession() {
        if (isTracking) {
            isTracking = false
            _endTime.value = System.currentTimeMillis()
            locationTracker.stopLocationUpdates()
            stepCounter.unregisterStepCounterListener()
            stopTimer()

        }
    }

    fun observeLocationData() {
        CoroutineScope(locationTracker.scope.coroutineContext).launch {
            locationTracker.currentLocation.collect { currentLocation ->
                if (currentLocation != null && _lastLocation.value == null) {
                    // Az első érvényes helyzet lesz az első helyzet
                    firstLocation = currentLocation
                    println("firstLocation: $firstLocation")
                }

                if (currentLocation != null && currentLocation != _lastLocation.value) {
                    _previousLocation.value = lastLocation.value
                    println("previousloc: ${_previousLocation.value}")
                    _lastLocation.value = currentLocation
                    println("lastlocation: ${_lastLocation.value}")
                    println("CurrentLocation: $currentLocation")

                }
                if (currentLocation != null) {
                    updateSessionData()
                }
            }
        }
    }

    private fun isUserNearEndPoint(): Boolean {
        val lastLocationValue = lastLocation.value
        val firstLocationValue = firstLocation
        println("first location: $firstLocation")
        println("last location: $lastLocationValue")

        if (lastLocationValue != null && firstLocationValue != null) {
            val distance = lastLocationValue.distanceTo(firstLocationValue)
            if (distanceTravelled.value > Constants.DISTANCE_TRAVELLED_THRESHOLD && distance < Constants.END_POINT_PROXIMITY_THRESHOLD) {
                return true
            }
        }
        return false
    }

    private fun updateSessionData() {
        CoroutineScope(Dispatchers.Default).launch {
            val incrementalDistance =
                fitnessCalculator.calculateDistanceBetweenPoints(
                    previousLocation.value,
                    lastLocation.value
                )
            //println("Location list size: " + locationList.value.size)
            println("Distance $incrementalDistance")
            _distanceTravelled.emit(_distanceTravelled.value + incrementalDistance)
            _duration.emit(elapsedTime)
            println("Elapsed: " + elapsedTime)

            println("averageSpeed: ${averageSpeed.value}")
            _averageSpeed.emit(
                fitnessCalculator.calculateAverageSpeed(
                    distanceTravelled.value,
                    elapsedTime
                )
            )

            _stepsTaken.emit(stepCounter.getStepCount())
            //println("stepsTaken: ${stepsTaken.value}")

            val calorieBurned = fitnessCalculator.calculateCaloriesBurned(
                averageSpeed.value,
                (elapsedTime / 60000).toDouble()
            )
            _calorieBurned.emit(calorieBurned)
            println("calorieBurned: $calorieBurned")
            //_heartRate = fitnessCalculator.calculateHeartRate()
            //_averageHeartRate = fitnessCalculator.calculateAverageHeartRate()

            if (isUserNearEndPoint()) {
                stopSession()
                println("latakia")
                _isSessionFinished.emit(true)
            }
        }
    }

    /*private fun saveUserRouteToDatabase(route: Route){
        userRouteRepository.saveUserRouteToDatabase(route)
    }*/
}

