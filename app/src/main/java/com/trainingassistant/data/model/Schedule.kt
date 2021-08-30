package com.trainingassistant.data.model

import com.trainingassistant.asListOfType
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor


sealed class Schedule(val duration: Int) {
    class Weekly(
        val frequency: Int,
        val scheduledSessions: List<ScheduleSession>,
        duration: Int,
    ): Schedule(duration) {
        override val scheduleTypeText = "Weekly"
        override val scheduleDetails = "$frequency/week\n${
            scheduledSessions.joinToString("\n") { it.text }
        }"
    }
    class Monthly(
        val frequency: Int,
        duration: Int,
    ): Schedule(duration) {
        override val scheduleTypeText = "Monthly"
        override val scheduleDetails =  "$frequency/month"
    }
    class OnDemand (duration: Int = 60): Schedule(duration) {
        override val scheduleTypeText = "On-Demand"
        override val scheduleDetails =  ""
    }
    data class ScheduleSession(val day: Int, val time: Int, val duration: Int) {
        private val _dateFormat = DateTimeFormatter.ofPattern("h:mm a")
        val text: String
            get() {
                val start = LocalTime.of(time / 60, time.mod(60))
                val end = start.plusMinutes(duration.toLong())
                return "${daysText[day]} ${start.format(_dateFormat)} - ${end.format(_dateFormat)}"
            }
    }

    abstract val scheduleTypeText: String
    abstract val scheduleDetails: String

    companion object {
        private fun intTimeToString(time: Int) = "${floor(time / 60.0)}: ${time.mod(60)}"
        val daysText = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        fun fromDoc(data: Map<String, Any>): Schedule {
            if (data.isEmpty())
                throw Exception("Empty schedule map from client query")
            if (!data.containsKey("type"))
                throw Exception("Missing type key in schedule map")

            return when(data["type"] as String) {
                "weekly" -> Weekly(
                    (data["frequency"] as Long).toInt(),
                    (data.getOrDefault("sessions", listOf<Map<String, Any>>()) as List<*>)
                        .asListOfType<Map<String, Any>>()
                        ?.map { session ->
                            ScheduleSession(
                                (session["day"] as Long).toInt(),
                                (session["time"] as Long).toInt(),
                                (session["duration"] as Long).toInt()
                            )
                        } ?: listOf(),
                    (data["duration"] as Long).toInt(),
                )
                "monthly" -> Monthly(
                    (data["frequency"] as Long).toInt(),
                    (data.getOrDefault("duration",0L) as Long).toInt()
                )
                else -> OnDemand(
                    (data.getOrDefault("duration",0L) as Long).toInt()
                )
            }
        }
    }
}
