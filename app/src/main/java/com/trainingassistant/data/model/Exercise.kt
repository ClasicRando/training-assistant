package com.trainingassistant.data.model

import com.trainingassistant.asListOfType

class Exercise private constructor(
    val id: String,
    val name: String,
    val type: String,
    val primary: String,
    val secondary: List<String>,
) {
    companion object {
        val empty = Exercise("", "", "", "", listOf())
        private val requiredKeys = listOf(
            "name",
            "primary",
            "secondary",
            "type"
        )
        fun fromDoc(id: String, data: Map<String, Any>): Exercise {
            if (data.isEmpty()) {
                throw Exception("Empty document from exercise query")
            }
            requiredKeys.forEach { key ->
                if (!data.containsKey(key)) {
                    throw Exception("Missing $key key in exercise doc")
                }
            }
            return Exercise(
                id,
                data["name"] as String,
                data["type"] as String,
                data["primary"] as String,
                (data["secondary"] as List<*>).asListOfType() ?: listOf()
            )
        }
    }
}