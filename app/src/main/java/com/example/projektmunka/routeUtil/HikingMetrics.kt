package com.example.projektmunka.routeUtil


import com.example.projektmunka.data.Node
import com.example.projektmunka.data.Route
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultWeightedEdge
import android.location.Location
import kotlin.math.abs
import kotlin.math.sqrt

fun calculateRouteAscent(route: Route): Double {
    var totalAscent = 0.0

    for (i in 0..route.path.size - 2) {
        val ascent = route.path[i + 1].elevation - route.path[i].elevation
        if (ascent > 0) {
            totalAscent += ascent
        }
    }
    return totalAscent
}

// (miles / 3) + (feet / 2000)
fun NaismitsRule(graph: Graph<Node, DefaultWeightedEdge>, route: Route): Double {
    val distanceInMiles = calculateRouteLength(graph, route) * 0.621371192
    val ascentInFeet = calculateRouteAscent(route) * 0.3048
    return ((distanceInMiles / 3) + (ascentInFeet / 2000)) * 60
}

// km, m
fun MunterMethod(graph: Graph<Node, DefaultWeightedEdge>, route: Route, rate: Double): Double {
    return (calculateRouteLength(graph, route) + (calculateRouteAscent(route) / 100)) / rate
}

// feet, miles
fun ShenandoahsHikingDifficulty(
    graph: Graph<Node, DefaultWeightedEdge>,
    route: Route
): Double {
    val distanceInMiles = calculateRouteLength(graph, route) * 0.621371192
    val ascentInFeet = calculateRouteAscent(route) * 0.3048
    return sqrt(ascentInFeet * 2 * distanceInMiles)
}

fun WeightedRouteDifficulty(
    graph: Graph<Node, DefaultWeightedEdge>,
    route: Route) : Double {
    return sqrt(calculateRouteAscent(route) * 1 + calculateRouteLength(graph, route) * 20)
}

fun calculateRouteGradientForSegment(graph: Graph<Node, DefaultWeightedEdge>, source: Node, target: Node) : Double {
    val AD = abs(target!!.elevation - source!!.elevation)
    val Dist = graph.getEdgeWeight(graph.getEdge(source, target))
    return  AD / Dist * 100
}

fun calculateRouteGradientForSegment(source: Location, target: Location) : Double {
    val AD = abs(target!!.altitude - source!!.altitude)
    val results = FloatArray(1)
    Location.distanceBetween(source.latitude, source.longitude, target.latitude, target.longitude, results)
    val dist = results[0].toDouble()
    return  AD / dist * 100
}

fun calculateWalkingSpeedForSegment(graph: Graph<Node, DefaultWeightedEdge>, source: Node, target: Node) : Double {
    val gradient = calculateRouteGradientForSegment(graph, source, target)

    if (gradient >= 4 && gradient < 8) {
        return 70.0;
    }
    else if (gradient >= 0 && gradient < 4) {
        return 80.0;
    }
    else if (gradient >= -4 && gradient < 0) {
        return 90.0;
    }
    else if (gradient >= -8 && gradient < -4) {
        return 100.0;
    }

    return 85.0;
}

// m/perc
fun calculateWalkingSpeedByAge(age: Int, source: Location, target: Location): Double {
    val walkingSpeedForSegment = calculateWalkingSpeedForSegment(source, target)
    val multiply = walkingSpeedForSegment/ 85.0

    var baseSpeed: Double = if (age >= 60)
        72.6
    else if (age in 50..59)
        73.8
    else if (age in 30..49)
        75.6
    else
        80.4

    return baseSpeed * multiply
}
fun fuggveny (age: Int, source: Location, target: Location, gender: Boolean): Double {
// valamekkora gradientnél a 85-höz képest hány százalékkal több vagy kevesebb a sétálási sebesség
    // pl 2-es gradient a 85-höz képest 1,15 %-kal lesz több a sétálási sebesség
    // age és gender alapján kikeressük, hogy milyen érték tartozik ahhoz

    val speed = calculateWalkingSpeedForSegment(source, target)
    val multiply = speed / 85.0
    // Másik táblázat alapján ugyanaz, mint a segmentes fgv.
    val baseSpeed = 70.0
    return baseSpeed * multiply
}

//m/perc
// valamekkora gradientnél a 85-höz képest hány százalékkal több vagy kevesebb a sétálási sebesség
fun calculateWalkingSpeedForSegment(source: Location, target: Location) : Double {
    val gradient = calculateRouteGradientForSegment(source, target)

    if (gradient >= 4 && gradient < 8) {
        return 70.0;
    }
    else if (gradient >= 0 && gradient < 4) {
        return 80.0;
    }
    else if (gradient >= -4 && gradient < 0) {
        return 90.0;
    }
    else if (gradient >= -8 && gradient < -4) {
        return 100.0;
    }

    return 85.0;
}

fun Node.toLocation(): Location {
    val location = Location("")
    location.latitude = this.lat
    location.longitude = this.lon
    return location
}

// ez kell
fun calculateWalkingSpeed(graph: Graph<Node, DefaultWeightedEdge>, route: Route) : Double {
    var speed = 0.0
    for (i in 0 until route.path.size - 1) {
        val source = route.path[i]
        val target = route.path[i + 1]
        speed += calculateWalkingSpeedForSegment(graph, source!!, target!!)
    }
    return speed / route.path.size
}

// ez kell
fun calculateWalkingSpeed(locationData: MutableList<Location?>) : Double {
    var speed = 0.0
    for (i in 0 until locationData.size - 1) {
        val source = locationData[i]
        val target = locationData[i + 1]
        speed += calculateWalkingSpeedForSegment(source!!, target!!)
    }
    return speed / locationData.size
}

// return is in minutes
fun calculateWalkingTime(graph: Graph<Node, DefaultWeightedEdge>, route: Route) : Double {
    var time = 0.0
    for (i in 0 until route.path.size - 1) {
        val source = route.path[i]
        val target = route.path[i + 1]
        time += graph.getEdgeWeight(graph.getEdge(source, target)) * 1000 / calculateWalkingSpeedForSegment(graph, source!!, target!!)
    }
    return time
}

fun calculateWalkingTime(locationData: MutableList<Location?>) : Double {
    var time = 0.0
    for (i in 0 until locationData.size - 1) {
        val source = locationData[i]
        val target = locationData[i + 1]

        val results = FloatArray(1)
        Location.distanceBetween(source!!.latitude, source.longitude, target!!.latitude, target.longitude, results)
        val dist = results[0].toDouble()
        time += dist * 1000 / calculateWalkingSpeedForSegment(source!!, target!!)
    }
    return time
}

fun calculateCaloriesBurned(graph: Graph<Node, DefaultWeightedEdge>, route: Route) : Double {
    val weightInKg = 57.0 //user testtömege
    return (calculateMETValue(calculateWalkingSpeed(graph, route) * weightInKg * calculateWalkingTime(graph, route) / 200))
}

fun calculateCaloriesBurned(locationData: MutableList<Location?>) : Double {
    val weightInKg = 57.0 //user testtömege
    return (calculateMETValue(calculateWalkingSpeed(locationData) * weightInKg * calculateWalkingTime(locationData) / 200))
}

fun calculateMETValue(speed : Double) : Double {
    return when {
        speed < 70 -> 3.1 // 0.894 m/s is approximately 2 mph
        speed < 80 -> 3.3  // Casual walking
        speed < 90 -> 3.6 // Brisk walking
        speed < 100 -> 4.0 // Fast-paced walking
        else -> 4.0 // Assuming 4.0 METs for speeds above 5 m/s
    }
}


fun calculateDifficultyLevelChange(heartrate : Double, age : Int) : Int {
    val percantage = (heartrate / (220.0 - age)) * 100
    return when {
        percantage < 50 -> 1 // növelni kell a terhelést
        percantage < 85 -> 0 // pont jó terhelés, nem kell változtatni
        else -> -1 // túl megterhelő, csökkenteni kell a terhelést
    }
}