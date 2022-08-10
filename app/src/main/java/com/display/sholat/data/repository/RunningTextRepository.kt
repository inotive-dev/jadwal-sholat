package com.display.sholat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.display.sholat.data.db.AppDatabase
import com.display.sholat.data.entity.RunningText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RunningTextRepository @Inject constructor(database: AppDatabase) {

    private val runningTextDao = database.runningTextDao()

    fun getAll() : LiveData<List<RunningText>> {
        val liveData = MutableLiveData<List<RunningText>>()
        CoroutineScope(Dispatchers.IO).launch {
            val all = runningTextDao.getAll()
            launch(Dispatchers.Main) {
                liveData.value = all
            }
        }
        return liveData
    }

    fun getAllWith() : LiveData<List<RunningText>> {
        val liveData = MutableLiveData<List<RunningText>>()
        CoroutineScope(Dispatchers.IO).launch {
            val all = runningTextDao.getAllWith()
            launch(Dispatchers.Main) {
                liveData.value = all
            }
        }
        return liveData
    }

    fun getAllWithIqomah() : LiveData<List<RunningText>> {
        val liveData = MutableLiveData<List<RunningText>>()
        CoroutineScope(Dispatchers.IO).launch {
            val all = runningTextDao.getAllWithIqomah()
            launch(Dispatchers.Main) {
                liveData.value = all
            }
        }
        return liveData
    }

    fun getCurrent() : LiveData<RunningText?> {
        val liveData = MutableLiveData<RunningText?>()
        CoroutineScope(Dispatchers.IO).launch {
            val runningText = runningTextDao.getCurrent()
            launch(Dispatchers.Main) {
                liveData.value = runningText
            }
        }
        return liveData
    }

    fun inserts(list: List<RunningText>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list.size == 1) runningTextDao.insert(list[0])
            else runningTextDao.inserts(list)
        }
    }

    fun updates(list: List<RunningText>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list.size == 1) runningTextDao.update(list[0])
            else runningTextDao.updates(list.toTypedArray())
        }
    }

    fun deletes(list: List<RunningText>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list.size == 1) runningTextDao.delete(list[0])
            else runningTextDao.deletes(list.map { it.id }.toTypedArray())
        }

    }
}