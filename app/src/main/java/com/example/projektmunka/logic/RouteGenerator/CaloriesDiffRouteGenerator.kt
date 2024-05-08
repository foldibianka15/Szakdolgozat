package com.example.projektmunka.logic.RouteGenerator

import GeneratePaths
import com.example.projektmunka.routeUtil.addressConverter
import com.example.projektmunka.routeUtil.calculateCaloriesBurned
import com.example.projektmunka.routeUtil.findNearestNodeInBbox
import com.example.projektmunka.routeUtil.getGraph
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.UserRouteTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CaloriesDiffRouteGenerator @Inject constructor(private val userRouteTracker: UserRouteTracker) {

    suspend fun startTrackingCaloriesDiff(
        sourceAddress: String,
        destinationAddress: String,
        targetCalorieMetric: Double
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            userRouteTracker.lastLocation.collect { locationData ->
                val source: Pair<Double, Double> = addressConverter(sourceAddress, locationData)
                val destination: Pair<Double, Double> = addressConverter(destinationAddress)
                userRouteTracker.generateDiffRoute {
                    generateCaloriesDifficultRoute(
                        source,
                        destination,
                        targetCalorieMetric
                    )
                }
            }
        }
    }

    suspend fun generateCaloriesDifficultRoute(
        source: Pair<Double, Double>,
        destination: Pair<Double, Double>,
        targetDifficulty: Double
    ): Route {

        val startNode = withContext(Dispatchers.IO) {
            findNearestNodeInBbox(source.first, source.second)
        } ?: throw Exception("Failed to find nearest node for source")

        val endNode = withContext(Dispatchers.IO) {
            findNearestNodeInBbox(destination.first, destination.second)
        } ?: throw Exception("Failed to find nearest node for destination")

        val graph = getGraph(startNode, endNode) ?: throw Exception("Failed to get graph")

        val bestRoute = GeneratePaths(
            graph,
            startNode,
            endNode,
            300,
            ::calculateCaloriesBurned,
            targetDifficulty,
            0.0
        )

        return bestRoute
    }
}