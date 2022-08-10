package com.display.sholat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.display.sholat.data.db.AppDatabase
import com.display.sholat.data.entity.SlideShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SlideShowRepository @Inject constructor(database: AppDatabase) {

    private val slideShowDao = database.slideShowDao()

    fun getAll() : LiveData<List<SlideShow>> {
        val liveData = MutableLiveData<List<SlideShow>>()
        CoroutineScope(Dispatchers.IO).launch {
            val all = slideShowDao.getAll()
            launch(Dispatchers.Main) {
                liveData.value = all
            }
        }
        return liveData
    }

    fun getAllWith() : LiveData<List<SlideShow>> {
        val liveData = MutableLiveData<List<SlideShow>>()
        CoroutineScope(Dispatchers.IO).launch {
            val all = slideShowDao.getAllWith()
            launch(Dispatchers.Main) {
                liveData.value = all
            }
        }
        return liveData
    }

    fun getWithIqomah() : LiveData<SlideShow> {
        val liveData = MutableLiveData<SlideShow>()
        CoroutineScope(Dispatchers.IO).launch {
            val item = slideShowDao.getWithIqomah()
            launch(Dispatchers.Main) {
                liveData.value = item
            }
        }
        return liveData
    }


    fun deletes(list: List<SlideShow>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list.size == 1) slideShowDao.delete(list[0])
            else slideShowDao.deletes(list.map { it.id }.toTypedArray())
        }
    }

    fun updates(list: List<SlideShow>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list.size == 1) slideShowDao.update(list[0])
            else slideShowDao.updates(list.toTypedArray())
        }
    }

    fun inserts(list: List<SlideShow>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (list.size == 1) slideShowDao.insert(list[0])
            else slideShowDao.inserts(list)
        }
    }

    fun update(slideShow: SlideShow) {
        CoroutineScope(Dispatchers.IO).launch {
            slideShowDao.update(slideShow)
        }
    }

}