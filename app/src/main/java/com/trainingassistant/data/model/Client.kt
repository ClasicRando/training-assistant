package com.trainingassistant.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField
import com.trainingassistant.asMapOfType
import com.trainingassistant.milliseconds
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Client private constructor(
    val id: String,
    private val clientId: String,
    val name: String,
    val schedule: Schedule,
    private val startDate: Timestamp,
    private val endDate: Timestamp,
    val bankedSessions: Int = 0,
) {

    val isActive = endDate.milliseconds > Timestamp.now().milliseconds
    val isClientLinked = clientId.isEmpty()
    fun startDateLocalText(zoneId: ZoneId, dateFormat: DateTimeFormatter): String {
        return Instant.ofEpochSecond(startDate.seconds).atZone(zoneId).format(dateFormat)
    }
    fun endDateLocalText(zoneId: ZoneId, dateFormat: DateTimeFormatter): String {
        return Instant.ofEpochSecond(endDate.seconds).atZone(zoneId).format(dateFormat)
    }
    fun startDateLocal(zoneId: ZoneId): ZonedDateTime {
        return Instant.ofEpochSecond(startDate.seconds).atZone(zoneId)
    }
    fun endDateLocal(zoneId: ZoneId): ZonedDateTime {
        return Instant.ofEpochSecond(endDate.seconds).atZone(zoneId)
    }

    fun with(
        clientId: String? = null,
        name: String? = null,
        schedule: Schedule? = null,
        startDate: Timestamp? = null,
        endDate: Timestamp? = null,
        bankedSessions: Int? = null
    ): Client {
        return Client(
            id,
            clientId ?: this.clientId,
            name ?: this.name ,
            schedule ?: this.schedule,
            startDate ?: this.startDate,
            endDate ?: this.endDate,
            bankedSessions ?: this.bankedSessions
        )
    }

    companion object {
        private val requiredKeys = listOf(
            "clientId",
            "name",
            "schedule",
            "startDate",
            "endDate",
            "bankedSessions"
        )
        fun fromDoc(document: DocumentSnapshot?): Client {
            if (document == null)
                return empty
            if (!document.exists()) {
                throw Exception("Document from session query does not exist")
            }
            val data = document.data ?: mapOf()
            if (data.isEmpty()) {
                throw Exception("Empty document from client query")
            }
            requiredKeys.forEach { key ->
                if (!data.containsKey(key)) {
                    throw Exception("Missing $key key in client doc")
                }
            }
            return Client(
                document.id,
                document.getString("clientId") ?: "",
                document.getString("name") ?: "",
                Schedule.fromDoc(
                    (document.data?.getOrDefault("schedule", mapOf<String, Any>()) as Map<*, *>)
                        .asMapOfType()
                        ?: mapOf("type" to "nothing")
                ),
                document.getTimestamp("startDate") ?: Timestamp.now(),
                document.getTimestamp("endDate") ?: Timestamp.now(),
                document.getField("bankedSessions") ?: 0,
            )
        }
        val empty = Client(
            id = "",
            clientId = "",
            name = "",
            schedule = Schedule.OnDemand(),
            startDate = Timestamp.now(),
            endDate = Timestamp.now(),
        )
    }
}