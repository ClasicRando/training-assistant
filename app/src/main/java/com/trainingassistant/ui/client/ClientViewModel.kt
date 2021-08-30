package com.trainingassistant.ui.client

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingassistant.data.Queries
import com.trainingassistant.data.model.Client
import com.trainingassistant.data.model.Session
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {

    private val _queries = Queries.instance
    private var _id: String = ""
    val id: String
        get() = _id
    val adapter = ClientSessionAdapter(this) { position ->
        _sessionSelectedId.value = getSession(position).id
    }

    private val _client = MutableLiveData<Client>()
    val client: LiveData<Client>
        get() = _client
    private val _sessions = MutableLiveData<List<Session>>()
    val sessions: LiveData<List<Session>>
        get() = _sessions
    private val _sessionSelectedId = MutableLiveData<String>()
    val sessionSelectedId: LiveData<String>
        get() = _sessionSelectedId
    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress
    val sessionCount
        get() = _sessions.value?.size ?: 0

    fun setClientId(id: String) {
        _id = id
    }

    fun getSession(position: Int) = _sessions.value?.get(position) ?: Session.empty

    fun refreshClient() {
        _showProgress.value = true
        viewModelScope.launch {
            _client.postValue(_queries.getClient(id))
            _sessions.postValue(_queries.getClientSessions(id))
            _showProgress.postValue(false)
        }
    }
}