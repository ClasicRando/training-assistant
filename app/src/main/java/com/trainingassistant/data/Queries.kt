package com.trainingassistant.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.trainingassistant.data.model.Client
import com.trainingassistant.data.model.Session
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class Queries private constructor() {

    private val _auth = FirebaseAuth.getInstance()
    private val _db = Firebase.firestore
    private val _zoneId = ZoneId.systemDefault()

    suspend fun getSessions(date: LocalDateTime): List<Session> {
        return runCatching {
            val startOfDay = date.truncatedTo(ChronoUnit.DAYS)
                .atZone(_zoneId)
                .toInstant()
            val endOfDay = date.plusDays(1)
                .truncatedTo(ChronoUnit.DAYS)
                .atZone(_zoneId)
                .toInstant()
            _db.collection("sessions")
                .whereEqualTo("uid", _auth.currentUser!!.uid)
                .whereGreaterThan("daytime", Date.from(startOfDay))
                .whereLessThan("daytime", Date.from(endOfDay))
                .get()
                .await()
                .map { document -> Session.fromDoc(document) }
        }.getOrElse { t ->
            Log.e("sessions", t.stackTraceToString())
            listOf()
        }
    }

    suspend fun getSession(id: String): Session {
        return runCatching {
            Session.fromDoc(
                _db.document("sessions/$id")
                    .get()
                    .await()
            )
        }.getOrElse { t ->
            Log.e("sessions", t.stackTraceToString())
            Session.empty
        }
    }

    suspend fun getClients(): List<Client> {
        return runCatching {
            _db.collection("clients")
                .whereEqualTo("uid", _auth.currentUser!!.uid)
                .get()
                .await()
                .map { document -> Client.fromDoc(document) }
        }.getOrElse {  t ->
            Log.e("clients", t.stackTraceToString())
            listOf()
        }
    }

    suspend fun getClient(id: String): Client {
        return runCatching {
            Client.fromDoc(
            _db.document("clients/$id")
                .get()
                .await()
            )
        }.getOrElse {  t ->
            Log.e("clients", t.stackTraceToString())
            Client.empty
        }
    }

    suspend fun getClientSessions(clientId: String): List<Session> {
        return runCatching {
            _db.collection("sessions")
                .whereEqualTo("uid", _auth.currentUser!!.uid)
                .whereEqualTo("client", _db.document("clients/$clientId"))
                .get()
                .await()
                .map { document -> Session.fromDoc(document) }
        }.getOrElse {   t ->
            Log.e("clients", t.stackTraceToString())
            listOf()
        }
    }

    companion object {
        val instance = Queries()
    }
}