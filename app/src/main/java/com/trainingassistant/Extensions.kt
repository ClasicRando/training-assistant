package com.trainingassistant

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp

inline fun <reified  T> List<*>.asListOfType(): List<T>? {
    return if (all { it is T })
        @Suppress("UNCHECKED_CAST")
        this as List<T>
    else
        null
}

inline fun <reified T> Map<*, *>.asMapOfType(): Map<T, Any>? {
    return if (keys.all { it is T })
        @Suppress("UNCHECKED_CAST")
        this as Map<T, Any>
    else
        null
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
    itemView.setOnClickListener {
        event(adapterPosition)
    }
    return this
}

fun RecyclerView.ViewHolder.getStringResource(id: Int) = itemView.context.getString(id)

val Timestamp.milliseconds get() = seconds * 1000