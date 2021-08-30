package com.trainingassistant.ui.sessions

import androidx.lifecycle.*
import com.trainingassistant.data.Queries
import com.trainingassistant.data.model.Session
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SessionsViewModel(private val state: SavedStateHandle) : ViewModel() {

    val feedPosition = state.get<Int>("FEED_POSITION") ?: 0
    private val _queries = Queries.instance
    val adapter = SessionsAdapter(this) {
        _selectedSessionId.postValue(getSessionId(it))
    }

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean>
        get() = _showProgress
    private val _sessions = MutableLiveData(listOf<Session>())
    val sessions: LiveData<List<Session>>
        get() = _sessions
    private val _currentDate = MutableLiveData(LocalDateTime.now())
    val currentDate: LiveData<LocalDateTime>
        get() = _currentDate
    private val _selectedSessionId = MutableLiveData<String>()
    val selectedSessionId: LiveData<String>
        get() = _selectedSessionId

    val sessionCount: Int
        get() = _sessions.value?.size ?: 0

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        val newDate = _currentDate.value?.withYear(year)
            ?.withMonth(month + 1)
            ?.withDayOfMonth(dayOfMonth)
        _currentDate.value = newDate
        refreshSessions()
    }

    fun saveFeedPosition(position: Int) = state.set("FEED_POSITION", position)
    private fun getSessionId(position: Int): String = _sessions.value?.get(position)?.id ?: ""
    fun getSession(position: Int): Session = _sessions.value?.get(position) ?: Session.empty

    fun refreshSessions() {
        _showProgress.value = true
        viewModelScope.launch {
            _sessions.postValue(_queries.getSessions(_currentDate.value ?: LocalDateTime.now()))
            _showProgress.postValue(false)
        }
    }
}