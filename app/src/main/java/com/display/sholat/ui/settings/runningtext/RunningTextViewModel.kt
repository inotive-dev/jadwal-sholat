package com.display.sholat.ui.settings.runningtext

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.display.sholat.data.entity.RunningText
import com.display.sholat.data.repository.RunningTextRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class RunningTextViewModel @Inject constructor(
    private val repository: RunningTextRepository) : ViewModel() {

    private val _all = MutableLiveData<Unit>()
    private val _current = MutableLiveData<Unit>()
    private val _insert = ArrayList<RunningText>()
    private val _update = ArrayList<RunningText>()
    private val _delete = ArrayList<RunningText>()

    val responseAll = Transformations.switchMap(_all) { repository.getAllWith() }
    val responseCurrent = Transformations.switchMap(_current) { repository.getCurrent() }

    fun getCurrent() = viewModelScope.launch { _current.value = Unit }

    fun getAll() = viewModelScope.launch { _all.value = Unit }

    fun update(item: RunningText, m: Int) = viewModelScope.launch {
        val find = _update.findLast { it.id == item.id }
        if (find != null) {
            val t = when (m) {
                0 -> item.copy(scheduleStart = find.scheduleStart, speed = find.speed)
                1 -> item.copy(text = find.text, speed = find.speed)
                else -> item.copy(text = find.text, scheduleStart = find.scheduleStart)
            }
            
            _update.remove(find)
            _update.add(t)
        } else {
            val findInInsert = _insert.findLast { it.id == item.id }
            if (findInInsert != null) {
                val t = when (m) {
                    0 -> item.copy(scheduleStart = findInInsert.scheduleStart, speed = findInInsert.speed)
                    1 -> item.copy(text = findInInsert.text, speed = findInInsert.speed)
                    else -> item.copy(text = findInInsert.text, scheduleStart = findInInsert.scheduleStart)
                }
                _insert.remove(findInInsert)
                _insert.add(t)
            } else {
                _update.add(item)
            }
        }
    }

    fun delete(item: RunningText) = viewModelScope.launch { _delete.add(item) }

    fun insert(item: RunningText) = viewModelScope.launch { _insert.add(item) }

    fun saveAll() {
        if (_insert.size > 0) repository.inserts(_insert.toList())
        if (_update.size > 0) repository.updates(_update.toList())
        if (_delete.size > 0) repository.deletes(_delete.toList())
        _insert.clear()
        _update.clear()
        _delete.clear()
    }
}