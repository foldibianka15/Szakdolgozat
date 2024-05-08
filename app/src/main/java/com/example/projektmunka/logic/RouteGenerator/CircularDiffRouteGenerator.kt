package com.example.projektmunka.logic.RouteGenerator

import android.location.Location
import com.example.projektmunka.data.Node
import com.example.projektmunka.data.Route
import com.example.projektmunka.logic.GPXFileHandler
import com.example.projektmunka.logic.KMLFileHandler
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.routeOptimizer.CircularDifficultRouteOptimizer
import com.example.projektmunka.routeUtil.addressConverter
import com.example.projektmunka.routeUtil.calculateROpt
import com.example.projektmunka.routeUtil.calculateSearchArea
import com.example.projektmunka.routeUtil.evaluateNodes
import com.example.projektmunka.routeUtil.fetchCityGraph
import com.example.projektmunka.routeUtil.fetchNodes
import com.example.projektmunka.routeUtil.findClosestNonIsolatedNode
import com.example.projektmunka.routeUtil.findNearestNodeInRadius
import com.example.projektmunka.routeUtil.getElevationData
import com.example.projektmunka.routeUtil.selectImportantPOIs
import com.example.projektmunka.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CircularDiffRouteGenerator @Inject constructor(
    private val userRouteTracker: UserRouteTracker,
    private val gpxFileHandler: GPXFileHandler,
    private val kmlFileHandler: KMLFileHandler
) {

    suspend fun startTrackingCircularDiff(
        sourceAddress: String,
        maxWalkingTimeInHours: Double
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            userRouteTracker.lastLocation.collect { locationData ->
                var source: Pair<Double, Double> = addressConverter(sourceAddress, locationData)
                val routes = userRouteTracker.generateCircularDiffRoute {
                    generateCircularDifficultRoute(maxWalkingTimeInHours, source)

                }
                routes?.let { (route, pois) ->
                    route?.let {
                        gpxFileHandler.generateGPXFileWithTimeAndSpeed(route, 27)
                    }
                }
            }
        }
    }
}

suspend fun generateCircularDifficultRoute(
    maxWalkingTimeInHours: Double,
    currentLocation: Pair<Double, Double>
): Pair<Route, Route> {
    val desiredRouteLength = maxWalkingTimeInHours * 4
    val rOpt = calculateROpt(Constants.PEDESTRIAN_SPEED, maxWalkingTimeInHours)
    val searchArea = calculateSearchArea(rOpt)

    val (latitude, longitude) = currentLocation
    val location = Location("")
    location.latitude = latitude
    location.longitude = longitude

    val nearestNode = withContext(Dispatchers.IO) {
        findNearestNodeInRadius(location, Constants.SEARCH_RADIUS_NEAREST_NODE)
    } ?: throw Exception("Failed to find nearest node")

    val nodes = withContext(Dispatchers.IO) {
        fetchNodes(nearestNode.lat, nearestNode.lon, rOpt)
    } ?: throw Exception("Failed to fetch nodes")

    val evaluatedNodes = evaluateNodes(nodes)
    val importantPOIs =
        selectImportantPOIs(evaluatedNodes, Constants.MAX_DISTANCE_SEARCH_IMPORTANT_POIS)
    println("körte")
    val cityGraph = fetchCityGraph(nearestNode.lat, nearestNode.lon, rOpt)
        ?: throw Exception("Failed to fetch city graph")

    println("alma")
    withContext(Dispatchers.IO) { getElevationData(cityGraph) }
    println("alma2")
    val nearestNonIsolatedNode =
        findClosestNonIsolatedNode(
            cityGraph,
            nearestNode,
            Constants.EXIT_DiSTANCE_FIND_NEAREST_NON_ISOLATED_NODE
        )
            ?: throw Exception("Failed to find nearest non-isolated node")

    println("alma3")
    val poiToClosestNonIsolatedNode: MutableMap<Node, Node> = mutableMapOf()

    println("alma4")
    for (poi in importantPOIs) {
        val closestNonIsolatedNode = findClosestNonIsolatedNode(
            cityGraph,
            poi,
            Constants.EXIT_DiSTANCE_FIND_NEAREST_NON_ISOLATED_NODE
        )
            ?: throw Exception("Failed to find closest non-isolated node for POI")
        poiToClosestNonIsolatedNode[poi] = closestNonIsolatedNode
    }

    println("alma5")

    val generator = CircularDifficultRouteOptimizer(
        cityGraph,
        poiToClosestNonIsolatedNode,
        importantPOIs,
        Constants.NUM_KEY_POIS,
        desiredRouteLength,  //teszt
        10.0, //nehézség, mennyire legyen emelkedős az út
        searchArea, //teszt terület
        20,
        5,
        50
    )

    println("alma6")
    val bestRoute = generator.runGeneticAlgorithm(nearestNonIsolatedNode)
    println("alma7")
    val connectedRoute = generator.connectPois(nearestNonIsolatedNode, bestRoute, cityGraph)
    val nonIsolatedBestRoute =
        Route(bestRoute.path.map { poi -> poiToClosestNonIsolatedNode[poi]!! }.toMutableList())

    println("alma9")
    return Pair(connectedRoute, nonIsolatedBestRoute)
}
