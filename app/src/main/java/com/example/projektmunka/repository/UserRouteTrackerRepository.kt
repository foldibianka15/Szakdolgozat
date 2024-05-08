package com.example.projektmunka.repository

import com.example.projektmunka.data.UserRouteTracker
import com.example.projektmunka.remote.UserRouteTrackerDao
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class UserRouteTrackerRepository(
    private val userRouteTrackerDao: UserRouteTrackerDao
) {
    suspend fun saveSessionDataIntoFirestore(
        startDate: Long?, endDate: Long?, duration: Long, steps: Int, calories: Double,
        averageSpeed: Double, averageHeartRate: Double, isFinished: Boolean
    ): UserRouteTracker {

        val userRouteTracker = UserRouteTracker(
            startDate = startDate,
            endDate = endDate,
            duration = duration,
            steps = steps,
            calories = calories,
            averageSpeed = averageSpeed,
            averageHeartRate = averageHeartRate,
            isFinished = isFinished
        )

        println("userRouteTrackerRepo: ${userRouteTracker.calories}")

        userRouteTrackerDao.saveUserRouteTrackerIntoFirestore(userRouteTracker)
        return userRouteTracker
    }
}