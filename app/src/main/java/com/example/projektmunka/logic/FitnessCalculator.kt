package com.example.projektmunka.logic

import android.location.Location
import javax.inject.Inject

class FitnessCalculator @Inject constructor() {

    // méterben
    fun calculateDistanceBetweenPoints(previousLocation: Location?, currentLocation: Location?): Double {
        var incrementalDistance = 0.0
        val results = FloatArray(1)
        if (previousLocation != null) {
            if (currentLocation != null) {
                Location.distanceBetween(
                    previousLocation.latitude, previousLocation.longitude,
                    currentLocation.latitude, currentLocation.longitude,
                    results
                )
                incrementalDistance = results[0].toDouble()
                println("incrementalDistance: $incrementalDistance")

            }
        }
        return incrementalDistance
    }

    fun calculateAverageSpeed(distanceTravelled: Double, elapsedTimeInSeconds: Long): Double {
        if (elapsedTimeInSeconds == 0L) return 0.0
        val elapsedTimeSeconds =
            elapsedTimeInSeconds.toDouble() / 1000 // milliszekundumokból másodpercekbe konvertálás
        return (distanceTravelled / elapsedTimeSeconds) * 3.6 // ha km/h, akkor ezt szorozd meg 3.6-al
    }

    fun calculateCaloriesBurned(averageSpeed: Double, walkingTimeInMinutes: Double): Double {
        val weightInKg = 57.0 //user testtömege
        val averageSpeedMPerMin = averageSpeed * (1000.0 / 60.0)
        return calculateMETValue(averageSpeedMPerMin) * weightInKg * walkingTimeInMinutes / 200
    }

    fun calculateMETValue(speed: Double): Double {
        return when {
            speed < 70 -> 3.1 // 0.894 m/s is approximately 2 mph
            speed < 80 -> 3.3  // Casual walking
            speed < 90 -> 3.6 // Brisk walking
            speed < 100 -> 4.0 // Fast-paced walking
            else -> 4.0 // Assuming 4.0 METs for speeds above 5 m/s
        }
    }

    fun calculateHeartRate(): Int {
        return 0
    }

    fun calculateAverageHeartRate(): Double {
        return 0.0
    }
}