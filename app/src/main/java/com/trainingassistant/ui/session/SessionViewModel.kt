package com.trainingassistant.ui.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingassistant.data.Queries
import com.trainingassistant.data.model.Session
import com.trainingassistant.data.model.SessionExercise
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {

    private val _queries = Queries.instance
    private var _id: String = ""
    val id: String
        get() = _id

    val adapter = SessionExerciseAdapter(this) {

    }
    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress
    private val _session = MutableLiveData<Session>()
    val session: LiveData<Session>
        get()  = _session
    val exerciseCount: Int
        get() = _session.value?.exercises?.size ?: 0

    fun setSessionId(id: String) {
        _id = id
    }

    fun getExercise(position: Int): SessionExercise =
        _session.value?.exercises?.get(position) ?: SessionExercise.empty

    fun refreshSession() {
        _showProgress.value = true
        viewModelScope.launch {
            _session.postValue(_queries.getSession(id))
            _showProgress.postValue(false)
        }
    }
}