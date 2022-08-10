package com.display.sholat.ui.settings.background

import androidx.lifecycle.*
import com.display.sholat.data.entity.SlideShow
import com.display.sholat.data.repository.SlideShowRepository
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SlideViewModel @Inject constructor(private val repository: SlideShowRepository) : ViewModel() {


    private val _slideShows = MutableLiveData<Unit>()
    private val _insert = ArrayList<SlideShow>()
    private val _update = ArrayList<SlideShow>()
    private val _delete = ArrayList<SlideShow>()

    val responseSlideShows = Transformations.switchMap(_slideShows) { repository.getAllWith() }

    fun getAll() = viewModelScope.launch { _slideShows.value = Unit }

    fun save(slideShow: SlideShow) = viewModelScope.launch {
        _insert.add(slideShow)
    }

    fun delete(item: SlideShow) = viewModelScope.launch {
        _delete.add(item)
    }

    fun update(item: SlideShow) = viewModelScope.launch {
        _update.add(item)
    }

    fun saveAll() {
        if (_insert.size > 0) repository.inserts(_insert.toList())
        if (_update.size > 0) repository.updates(_update.toList())
        if (_delete.size > 0) {
            repository.deletes(_delete.toList())
            _delete.forEach {
                val file = File(it.fileName)
                if (file.isFile) { file.delete() }
            }
        }
        _insert.clear()
        _update.clear()
        _delete.clear()
    }
}