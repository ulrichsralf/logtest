package io.mc.logconverter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.net.URL

/**
 * @author Ralf Ulrich
 * 22.07.18
 */
fun main(args: Array<String>) {


    val logs = URL("https://ralf-ulrich.com/log/tracking.log").readText()
    val entries = logs.split("\n").filter { it.contains("POST") }


    val mapper = jacksonObjectMapper()
    val timeToData = entries.map { it.splitToSequence(" ").take(3).last() to mapper.readValue(it.substringAfter("POST"), LogEntry::class.java) }



    timeToData.forEach {
        val offset = it.first.toLong() - it.second.currentClientDate
        val entry = it.second
        val events = entry.events.map {
            EventLog(it.activeUser ?: "",
                    it.clientDate + offset,
                    if (it.viewname == null) "CLICK" else "NAV",
                    it.viewname ?: it.buttonname ?: "")
        }.sortedBy { it.date }
        File("byDeviceId/").mkdirs()
        File("byUser/").mkdirs()
        val logFileD = File("byDeviceId/${entry.deviceUuid}").apply { createNewFile() }


        events.forEach {
            val logFileU = File("byUser/${it.user}").apply { createNewFile() }
            val text = mapper.writeValueAsString(it)
            logFileD.appendText(text)
            logFileU.appendText(text)
            logFileD.appendText("\n")
            logFileU.appendText("\n")
        }


    }

}


data class LogEntry(
        val deviceUuid: String,
        val currentClientDate: Long,
        val activeUser: String?,
        val events: List<Event>)

data class Event(val activeUser: String?,
                 val clientDate: Long,
                 val viewname: String?,
                 val buttonname: String?)

data class EventLog(val user: String, val date: Long, val type: String, val data: String)