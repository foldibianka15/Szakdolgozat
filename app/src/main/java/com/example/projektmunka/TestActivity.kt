package com.example.projektmunka

import GeneratePaths
import RouteMetric
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projektmunka.data.Node
import com.example.projektmunka.data.Route
import com.example.projektmunka.routeUtil.ShenandoahsHikingDifficulty
import com.example.projektmunka.routeUtil.callOverpass
import com.example.projektmunka.routeUtil.findNearestNodeInBbox
import com.example.projektmunka.routeUtil.getElevationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.math.sqrt

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val source = Pair<Double, Double>(47.534160,19.039650)
        val target = Pair<Double, Double>(47.555980,19.027330)

        val sourceLocation = Location("")
        sourceLocation.latitude = source.first
        sourceLocation.longitude = source.second

        val targetLocation = Location("")
        targetLocation.latitude = target.first
        targetLocation.longitude = target.second

        val distance = sourceLocation.distanceTo(targetLocation).toDouble()
        val maxDifficulty = maximumDifficulty(distance)
        println("Alma_Distance: $distance")
        println("Alma_MaxDifficulty: $maxDifficulty")

        for (i in 0 .. 10) {
            println("Calculated difficulty for user with fitness level ${i}: ${getDifficultyForUser(i.toDouble(), distance)}")
        }


        //val route =  GetRoute(source, target, 500, ::ShenandoahsHikingDifficulty, 30.0, 0.1)
    }

    val maxElevation = 100
    val maxDistance = 1.5
    val meterToFeet = 3.2808399
    val meterToMile = 0.000621371192
    fun maximumDifficulty(distanceInMeter: Double): Double {
        return sqrt(2 * maxElevation * meterToFeet * maxDistance * distanceInMeter * meterToMile)
    }

    fun getDifficultyForUser(fitnessLevel: Double, distanceInMeter: Double): Double {
        return maximumDifficulty(distanceInMeter) * (fitnessLevel / 10.0)
    }

    fun GetRoute(start: Pair<Double, Double>, end: Pair<Double, Double>, maxIterations : Int, metric: RouteMetric, targetMetric : Double, duplicatePunishment : Double) : Route? {
        return runBlocking {
            val startNode = async(Dispatchers.IO) {
                findNearestNodeInBbox(start.first, start.second)
            }.await() ?: return@runBlocking null

            val endNode = async(Dispatchers.IO) {
                findNearestNodeInBbox(end.first, end.second)
            }.await() ?: return@runBlocking null

            val minLat = (if (startNode.lat < endNode.lat) startNode.lat else endNode.lat) - 0.01
            val minLon = (if (startNode.lon < endNode.lon) startNode.lon else endNode.lon) - 0.01
            val maxLat = (if (startNode.lat > endNode.lat) startNode.lat else endNode.lat) + 0.01
            val maxLon = (if (startNode.lon > endNode.lon) startNode.lon else endNode.lon) + 0.01
            val bbox = "$minLat,$minLon,$maxLat,$maxLon"

            val graph = async(Dispatchers.IO) {
                callOverpass(bbox)
            }.await() ?: return@runBlocking null

            val elevations = async(Dispatchers.IO) {
                getElevationData(graph)
            }.await()

            val path = GeneratePaths(graph, startNode, endNode, maxIterations, metric, targetMetric, duplicatePunishment)
            return@runBlocking path
        }
    }
}