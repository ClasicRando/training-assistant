package com.trainingassistant.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.trainingassistant.data.Queries
import com.trainingassistant.data.model.Client
import kotlinx.coroutines.launch

class EditClientViewModel : ViewModel() {

    private val _queries = Queries.instance
    private var _id = ""
    val id
        get() = _id
    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress
    private val _client = MutableLiveData<Client>()
    val client: LiveData<Client>
        get() = _client

    fun setClientId(id: String) {
        _id = id
    }

    fun refreshClient() {
        _showProgress.value = true
        viewModelScope.launch {
            _client.postValue(_queries.getClient(id))
            _showProgress.postValue(false)
        }
    }

    fun updateClient() {
        _showProgress.value = true
        viewModelScope.launch {

            _client.postValue(_queries.getClient(id))
            _showProgress.postValue(false)
        }
    }

    fun updateStartDate(epochSecond: Long) {
        _client.value?.let { client ->
            _client.value = client.with(startDate = Timestamp(epochSecond, 0))
        }
    }

    fun updateEndDate(epochSecond: Long) {
        _client.value?.let { client ->
            _client.value = client.with(endDate = Timestamp(epochSecond, 0))
        }
    }
}