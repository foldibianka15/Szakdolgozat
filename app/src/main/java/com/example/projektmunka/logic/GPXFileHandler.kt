package com.example.projektmunka.logic

import android.content.Context
import android.location.Location
import com.example.projektmunka.data.Node
import com.example.projektmunka.data.Route
import com.example.projektmunka.routeUtil.calculateDistance
import com.example.projektmunka.routeUtil.calculateRouteGradientForSegment
import com.example.projektmunka.routeUtil.calculateWalkingSpeedByAge
import com.example.projektmunka.routeUtil.calculateWalkingSpeedForSegment
import com.example.projektmunka.routeUtil.toLocation
import java.io.File
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.math.pow

class GPXFileHandler @Inject constructor(private val context: Context) {

    fun generateGPXFileWithTimeAndSpeed(route: Route, age: Int) {
        val gpxBuilder = StringBuilder()
        gpxBuilder.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>")
        gpxBuilder.appendLine("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"YourAppName\">")
        gpxBuilder.appendLine("<trk>")
        gpxBuilder.appendLine("<name>Your Track Name</name>")
        gpxBuilder.appendLine("<trkseg>")

        //1714733590913
        //1714733592331
        var totalTime = System.currentTimeMillis()
        println("currentTimeMillis: $totalTime")
        var lastNode: Node? = null

        route.path.forEachIndexed { index, node ->
            if (index == 0) { // Az első Node esetén
                // Az első Node-ot azonnal kiírjuk a fájlba
                gpxBuilder.appendLine("<trkpt lat=\"${node.lat}\" lon=\"${node.lon}\">")
                gpxBuilder.appendLine("<ele>${node.elevation}</ele>")
                gpxBuilder.appendLine("<time>${calculateTime(totalTime)}</time>")
                gpxBuilder.appendLine("</trkpt>")
            } else {
                val distance = calculateDistance(route.path[index - 1], node)
                val walkingSpeed =
                    calculateWalkingSpeedByAge(age, route.path[index - 1].toLocation(), node.toLocation())
                val timeForSegmentInMilliSec =
                    distance / (walkingSpeed * (60.0 / 1000.0)) * 3600 * 1000
                totalTime += timeForSegmentInMilliSec.toLong()

                gpxBuilder.appendLine("<trkpt lat=\"${node.lat}\" lon=\"${node.lon}\">")
                gpxBuilder.appendLine("<ele>${node.elevation}</ele>")
                gpxBuilder.appendLine("<time>${calculateTime(totalTime)}</time>")
                gpxBuilder.appendLine("</trkpt>")
            }
        }

        gpxBuilder.appendLine("</trkseg>")
        gpxBuilder.appendLine("</trk>")
        gpxBuilder.appendLine("</gpx>")

        val fos = context.openFileOutput("routeGPX.gpx", Context.MODE_PRIVATE)
        fos.write(gpxBuilder.toString().toByteArray())
        fos.close()
        println("fos beírva")
    }


    fun calculateTime(timeMillis: Long): String {
        //val totalSeconds = totalTimeInMinutes * 60// Az összes időt másodpercekbe konvertáljuk
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        //return dateFormat.format(Date(totalSeconds.toLong() * 1000)) // Unix epoch időmértékkel dol
        //dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Az időzónát beállítjuk UTC-regozunk


        //val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        //dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Az időzónát beállítjuk UTC-re

        println("timemillis: $timeMillis")
        // define once somewhere in order to reuse it
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
// JVM representation of a millisecond epoch absolute instant
        val instant = Instant.ofEpochMilli(timeMillis)
        println("instant: $instant")
// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        println(" format: ${formatter.format(date)}") // 10/12/2019 06:35:45

        return formatter.format(date)
    }
}