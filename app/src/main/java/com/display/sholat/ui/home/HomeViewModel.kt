package com.display.sholat.ui.home

import androidx.lifecycle.*
import com.display.sholat.data.Preference
import com.display.sholat.data.repository.PrayerRepository
import com.display.sholat.data.repository.RunningTextRepository
import com.display.sholat.data.repository.SlideShowRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val prayerRepo: PrayerRepository,
    private val runningTextRepo: RunningTextRepository,
    private val slideShowRepo: SlideShowRepository,
    private val preference: Preference
): ViewModel() {

    private val _prayer = MutableLiveData<Unit>()
    private val _runningText = MutableLiveData<Unit>()
    private val _slideShow = MutableLiveData<Unit>()

    val responsePrayer  = Transformations.switchMap(_prayer) { prayerRepo.getPrayerWithLocal() }
    val responseRunningText = Transformations.switchMap(_runningText) { runningTextRepo.getAll() }
    val responseSlideShow = Transformations.switchMap(_slideShow) { slideShowRepo.getAll() }

    fun getUiModel() : LiveData<HomeUiModel> {
        val data = MutableLiveData<HomeUiModel>()
        data.value = HomeUiModel(
            preference.nameMosque?:"",
            preference.addressMosque?:"",
            preference.timeZoneId,
            preference.date,
            preference.dateHijri,
            arrayListOf(),
            responsePrayer.value,
            preference.iqomahNow, null, 0L, null)
        return data
    }

    fun getCurrentPrayer() = viewModelScope.launch { _prayer.value = Unit }

    fun getCurrentRunningText() = viewModelScope.launch { _runningText.value = Unit }

    fun getCurrentSlideShow() = viewModelScope.launch { _slideShow.value = Unit }


}