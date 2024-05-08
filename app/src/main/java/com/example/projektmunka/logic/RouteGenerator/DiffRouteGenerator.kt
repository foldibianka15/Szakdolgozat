package com.example.projektmunka.logic.RouteGenerator

import GeneratePaths
import com.example.projektmunka.routeUtil.ShenandoahsHikingDifficulty
import com.example.projektmunka.routeUtil.addressConverter
import com.example.projektmunka.routeUtil.findNearestNodeInBbox
import com.example.projektmunka.routeUtil.getGraph
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.routeUtil.calculateRouteAscent
import com.example.projektmunka.routeUtil.calculateRouteLength
import com.example.projektmunka.routeUtil.toLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.sqrt

class DiffRouteGenerator @Inject constructor(private val userRouteTracker: UserRouteTracker) {

    suspend fun startTrackingDiff(
        sourceAddress: String,
        destinationAddress: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            userRouteTracker.lastLocation.collect { locationData ->
                val source: Pair<Double, Double> = addressConverter(sourceAddress, locationData)
                val destination: Pair<Double, Double> = addressConverter(destinationAddress)
                userRouteTracker.generateDiffRoute {
                    generateDifficultRoute(source, destination, 8.0)
                }
            }
        }
    }

    suspend fun generateDifficultRoute(
        source: Pair<Double, Double>,
        destination: Pair<Double, Double>,
        targetDifficulty: Double  // user nehézségi szintje
    ): Route {
        val startNode = withContext(Dispatchers.IO) {
            findNearestNodeInBbox(source.first, source.second)
        } ?: throw Exception("Failed to find nearest node for source")

        val endNode = withContext(Dispatchers.IO) {
            findNearestNodeInBbox(destination.first, destination.second)
        } ?: throw Exception("Failed to find nearest node for destination")

        val graph = getGraph(startNode, endNode) ?: throw Exception("Failed to get graph")

        val distance = startNode.toLocation().distanceTo(endNode.toLocation()).toDouble()
        println("Test_Distance: $distance")
        val maxDifficulty = maximumDifficulty(distance)

        val difficulty = getDifficultyForUser(8.0, distance)
        println("Test_Difficulty: $difficulty")

        val bestRoute = GeneratePaths(
            graph, startNode, endNode, 300, ::ShenandoahsHikingDifficulty, difficulty, 0.1)

        println("Test_GeneratedRouteLenth: ${calculateRouteLength(bestRoute)}")
        println("Test_GeneratedRouteAscent: ${calculateRouteAscent(bestRoute)}")
        println("Test_GeneratedMaxDifficulty: $maxDifficulty")


        /* withContext(Dispatchers.Main) { // Assuming UI updates need to happen on the main thread
        drawRoute(osmMap, bestRoute)
        addMarker(osmMap, source.first, source.second)
        addMarker(osmMap, destination.first, destination.second)
    }*/

        return bestRoute
    }

    fun maximumDifficulty(distanceInMeter: Double): Double {
        val maxElevation = 100
        val maxDistance = 1.5
        val meterToFeet = 3.2808399
        val meterToMile = 0.000621371192
        return sqrt(2 * maxElevation * meterToFeet * maxDistance * distanceInMeter * meterToMile)
    }

    fun getDifficultyForUser(fitnessLevel: Double, distanceInMeter: Double): Double {
        return maximumDifficulty(distanceInMeter) * (fitnessLevel / 10.0)
    }
}