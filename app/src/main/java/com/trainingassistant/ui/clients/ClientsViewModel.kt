package com.trainingassistant.ui.clients

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.trainingassistant.data.Queries
import com.trainingassistant.data.model.Client
import kotlinx.coroutines.launch

class ClientsViewModel(private val state: SavedStateHandle) : ViewModel() {

    val feedPosition = state.get<Int>("FEED_POSITION") ?: 0
    private val _queries = Queries.instance
    val adapter = ClientsAdapter(this) { position ->
        _selectedClientId.postValue(getClientId(position))
    }

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress
    private val _selectedClientId = MutableLiveData<String>()
    val selectedClientId: LiveData<String>
        get() = _selectedClientId
    private val _clients = MutableLiveData<List<Client>>(listOf())
    val clients: LiveData<List<Client>>
        get() = _clients
    val clientCount
        get() = _clients.value?.size ?: 0

    fun saveFeedPosition(position: Int) = state.set("FEED_POSITION", position)
    private fun getClientId(position: Int) = _clients.value?.get(position)?.id ?: ""
    fun getClient(position: Int) = _clients.value?.get(position) ?: Client.empty

    fun refreshClients() {
        _showProgress.postValue(true)
        viewModelScope.launch {
            _clients.postValue(_queries.getClients())
            _showProgress.postValue(false)
        }
    }
}