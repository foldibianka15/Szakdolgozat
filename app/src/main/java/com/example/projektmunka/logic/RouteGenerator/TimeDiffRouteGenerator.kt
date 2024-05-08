package com.example.projektmunka.logic.RouteGenerator

import GenerateTimeDiffPaths
import com.example.projektmunka.routeUtil.NaismitsRule
import com.example.projektmunka.routeUtil.ShenandoahsHikingDifficulty
import com.example.projektmunka.routeUtil.addressConverter
import com.example.projektmunka.routeUtil.findNearestNodeInBbox
import com.example.projektmunka.routeUtil.getGraph
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.UserRouteTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimeDiffRouteGenerator @Inject constructor(private val userRouteTracker: UserRouteTracker) {

    suspend fun startTrackingTimeDiff(
        sourceAddress: String,
        destinationAddress: String,
        targetTimeMetric: Double
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            userRouteTracker.lastLocation.collect { locationData ->
                val source: Pair<Double, Double> = addressConverter(sourceAddress, locationData)
                val destination: Pair<Double, Double> = addressConverter(destinationAddress)
                userRouteTracker.generateDiffRoute {
                    generateTimeDifficultRoute(
                        source,
                        destination,
                        targetTimeMetric,
                        5.5
                    )
                }
            }
        }
    }

    suspend fun generateTimeDifficultRoute(
        source: Pair<Double, Double>,
        destination: Pair<Double, Double>,
        targetDifficulty: Double,
        targetTimeMetric: Double
    ): Route {
        val startNode = withContext(Dispatchers.IO) {
            findNearestNodeInBbox(source.first, source.second)
        } ?: throw Exception("Failed to find nearest node for source")

        val endNode = withContext(Dispatchers.IO) {
            findNearestNodeInBbox(destination.first, destination.second)
        } ?: throw Exception("Failed to find nearest node for destination")

        val graph = getGraph(startNode, endNode) ?: throw Exception("Failed to get graph")

        val bestRoute = GenerateTimeDiffPaths(
            graph,
            startNode,
            endNode,
            300,
            ::NaismitsRule,
            ::ShenandoahsHikingDifficulty,
            targetTimeMetric,
            targetDifficulty,
            5.0,
            0.0
        )

        return bestRoute
    }
}