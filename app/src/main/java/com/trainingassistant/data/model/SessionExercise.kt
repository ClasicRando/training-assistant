package com.trainingassistant.data.model

import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await

sealed class SessionExercise(
    val exercise: Exercise,
    val sets: Int,
    val comments: String
) {

    val commentsText = if (comments.isNotBlank()) "Comments: $comments" else ""

    class ResistanceSessionExercise(
        exercise: Exercise,
        sets: Int,
        val resistance: Int,
        val reps: Int,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            return "${exercise.name}\nSets: $sets\nResistance: $resistance\nReps: $reps"
        }
    }
    class IsometricSessionExercise(
        exercise: Exercise,
        sets: Int,
        val resistance: Int,
        val time: Int,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            return "${exercise.name}\nSets: $sets\nResistance: $resistance\nTime: $time"
        }
    }
    class MobilitySessionExercise(
        exercise: Exercise,
        sets: Int,
        val reps: Int,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            return "${exercise.name}\nSets: $sets\nReps: $reps"
        }
    }
    class StretchingSessionExercise(
        exercise: Exercise,
        sets: Int,
        val time: Int,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            return "${exercise.name}\nSets: $sets\nTime: $time"
        }
    }
    class EnduranceSessionExercise(
        exercise: Exercise,
        sets: Int,
        val time: Int,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            return "${exercise.name}\nSets: $sets\nTime: $time"
        }
    }
    class DistanceActivitySessionExercise(
        exercise: Exercise,
        sets: Int,
        val distance: Int,
        val uom: String,
        val time: Long,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            return "${exercise.name}\nSets: $sets\nDistance: $distance $uom\nTime: $time"
        }
    }
    class CustomSessionExercise(
        exercise: Exercise,
        sets: Int,
        val props: Map<String, Any>,
        comments: String = ""
    ) : SessionExercise(exercise, sets, comments) {
        override fun toString(): String {
            val properties = props.entries.joinToString("\n") { (key, value) ->
                "$key: $value"
            }
            return "${exercise.name}\nSets: $sets\n$properties"
        }
    }
    class Empty: SessionExercise(Exercise.empty, 0, "") {
        override fun toString(): String {
            return "Empty session used as placeholder. If you see this, you have found a bug"
        }
    }

    companion object {
        val empty = Empty()
        private val requiredKeys = listOf(
            "sets",
            "comments",
            "exercise"
        )
        suspend fun fromDoc(data: Map<String, Any>): SessionExercise {
            if (data.isEmpty()) {
                throw Exception("Empty exercise map from session exercise query")
            }
            requiredKeys.forEach { key ->
                if (!data.containsKey(key)) {
                    throw Exception("Missing $key key in session exercise doc")
                }
            }
            val document = (data["exercise"] as DocumentReference).get().await()
            val exercise = Exercise.fromDoc(document.id, document.data ?: mapOf())
            return when(exercise.type) {
                "resistance" -> ResistanceSessionExercise(
                    exercise,
                    (data["sets"] as Long).toInt(),
                    (data.getOrDefault("resistance", 0L) as Long).toInt(),
                    (data.getOrDefault("reps", 0L) as Long).toInt(),
                    data["comments"] as String,
                )
                "isometric" -> IsometricSessionExercise(
                    exercise,
                    (data["sets"] as Long).toInt(),
                    (data.getOrDefault("resistance", 0L) as Long).toInt(),
                    (data.getOrDefault("time", 0L) as Long).toInt(),
                    data["comments"] as String,
                )
                "mobility" -> MobilitySessionExercise(
                    exercise,
                    (data["sets"] as Long).toInt(),
                    (data.getOrDefault("reps", 0L) as Long).toInt(),
                    data["comments"] as String,
                )
                "stretching" -> StretchingSessionExercise(
                    exercise,
                    (data["sets"] as Long).toInt(),
                    (data.getOrDefault("time", 0L) as Long).toInt(),
                    data["comments"] as String,
                )
                "endurance" -> EnduranceSessionExercise(
                    exercise,
                    (data["sets"] as Long).toInt(),
                    (data.getOrDefault("time", 0L) as Long).toInt(),
                    data["comments"] as String,
                )
                "distance" -> DistanceActivitySessionExercise(
                    exercise,
                    (data["sets"] as Long).toInt(),
                    (data.getOrDefault("distance", 0L) as Long).toInt(),
                    data.getOrDefault("uom", "") as String,
                    data.getOrDefault("time", 0L) as Long,
                    data["comments"] as String,
                )
                else -> CustomSessionExercise(
                    exercise,
                    0,
                    mapOf(),
                )
            }
        }
    }
}
