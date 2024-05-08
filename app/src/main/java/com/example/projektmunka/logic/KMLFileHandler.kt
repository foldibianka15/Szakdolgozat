package com.example.projektmunka.logic

import android.content.Context
import android.location.Location
import com.example.projektmunka.data.Route
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

class KMLFileHandler@Inject constructor(private val context: Context) {

    private val TIME_INTERVAL = 1000

    // A mockolt időbélyegekhez és a KML fájlhoz szükséges változók
    private var currentTimeStamp = System.currentTimeMillis()
    private var timer: Timer? = null

    fun startTimer(route: Route) {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentTimeStamp += TIME_INTERVAL
                val kmlContent = buildKMLContent(route)
                writeKMLContentToFile(kmlContent)
            }
        }, 0, TIME_INTERVAL.toLong())
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    // Új időbélyeg generálása
    private fun generateTimeStamp(): String {
        val formattedTimeStamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .format(Date(currentTimeStamp))
        currentTimeStamp += TIME_INTERVAL // Időbélyeg növelése a következő iterációhoz
        return formattedTimeStamp
    }

    private fun buildKMLContent(route: Route): String {
        val sb = StringBuilder()
        sb.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        sb.appendLine("<kml xmlns=\"http://www.opengis.net/kml/2.2\">")
        sb.appendLine("  <Document>")
        sb.appendLine("    <name>YourRouteName</name>")

        for (node in route.path) {
            sb.appendLine("    <Placemark>")
            sb.appendLine("      <name>${generateTimeStamp()}</name>")
            sb.appendLine("      <Point>")
            sb.appendLine("        <coordinates>${node.lon},${node.lat},0</coordinates>")
            sb.appendLine("      </Point>")
            sb.appendLine("    </Placemark>")
        }

        sb.appendLine("  </Document>")
        sb.appendLine("</kml>")

        return sb.toString()
    }

    fun writeKMLContentToFile(content: String) {
        val fos = context.openFileOutput("routeKML.kml", Context.MODE_PRIVATE)
        fos.write(content.toByteArray())
        fos.close()
    }

    fun readKMLFile(filePath: String): List<Location> {
        val locations = mutableListOf<Location>()

        val file = File(filePath)
        if (file.exists()) {
            try {
                val br = file.bufferedReader()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    // Implement KML file parsing logic here if needed
                }
                br.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return locations
    }
}