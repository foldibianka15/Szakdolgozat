package com.example.projektmunka.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import com.google.firebase.Timestamp

data class UserRouteTracker(
    val id: String = "",
    var startDate: Long? = null,
    val endDate: Long? = null,
    val duration: Long,
    val steps: Int,
    val calories: Double,
    val averageSpeed: Double,
    val averageHeartRate: Double,
    val isFinished: Boolean = false
)