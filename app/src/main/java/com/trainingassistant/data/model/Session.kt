package com.trainingassistant.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.trainingassistant.asListOfType
import com.trainingassistant.milliseconds
import kotlinx.coroutines.tasks.await
import android.text.format.DateFormat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.getField
import com.trainingassistant.asMapOfType
import java.time.Duration

class Session private constructor(
    val id: String,
    val client: Client,
    private val dayTime: Timestamp,
    val duration: Int,
    val exercises: List<SessionExercise>,
    val notes: String
) {

    val itemTimeText: String
        get() {
            val start = DateFormat.format("h:mm a", dayTime.milliseconds)
            val end = DateFormat.format("h:mm a", dayTime.milliseconds + duration * 60 * 1000)
            return "$start - $end"
        }
    val date: String
        get() = DateFormat.format("d MMM, yyyy h:mm a", dayTime.milliseconds).toString()

    companion object {
        private val requiredKeys = listOf(
            "client",
            "exercises",
            "daytime",
            "duration",
            "notes"
        )
        suspend fun fromDoc(document: DocumentSnapshot): Session {
            if (!document.exists()) {
                throw Exception("Document from session query does not exist")
            }
            val data = document.data ?: mapOf()
            if (data.isEmpty()) {
                throw Exception("Empty document from session query")
            }
            requiredKeys.forEach { key ->
                if (!data.containsKey(key)) {
                    throw Exception("Missing $key key in session doc")
                }
            }
            val client = Client.fromDoc(
                document.getDocumentReference("client")
                    ?.get()
                    ?.await()
            )
            val exerciseData = document.data
                ?.getOrDefault("exercises", listOf<Map<String, Any>>()) as List<*>
            val sessionExercises = exerciseData
                .asListOfType<Map<String, Any>>()
                ?.map { SessionExercise.fromDoc(it) } ?: listOf()
            return Session(
                document.id,
                client,
                document.getTimestamp("daytime") ?: Timestamp.now(),
                document.getField("duration") ?: 0,
                sessionExercises,
                document.getString("notes") ?: ""
            )
        }
        val empty = Session("", Client.empty, Timestamp.now(), 0, listOf(), "")
    }

    override fun toString(): String {
        return "client:${client.name}\ndaytime: $dayTime\nduration: $duration\nnotes: $notes"
    }
}