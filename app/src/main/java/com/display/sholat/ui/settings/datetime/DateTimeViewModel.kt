package com.display.sholat.ui.settings.datetime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.display.sholat.App
import com.display.sholat.data.Preference
import kotlinx.coroutines.launch
import javax.inject.Inject

class DateTimeViewModel @Inject constructor(
    private val preference: Preference) : ViewModel() {

    private val _dateRangeTmp = MutableLiveData<String>()
    private val _hijriRangeTmp = MutableLiveData<String>()
    private val _timeZoneIdTmp = MutableLiveData<String>()

    fun saveDateRangeTmp(dateRange: String) = viewModelScope.launch {
        _dateRangeTmp.value = dateRange
    }

    fun saveTimeZoneIdTmp(timeZoneId: String) = viewModelScope.launch {
        _timeZoneIdTmp.value = timeZoneId
    }

    fun saveHijriRangeTmp(hijriRange: String) = viewModelScope.launch {
        _hijriRangeTmp.value = hijriRange
    }

    fun save() {
        _dateRangeTmp.value?.let {
            preference.date = it
        }
        _hijriRangeTmp.value?.let {
            preference.dateHijri = it
        }
        _timeZoneIdTmp.value?.let {
            preference.timeZoneId = it
            App.setTimeZoneId(it)
        }
    }

    fun getUiModel() : LiveData<DateTimeUiModel> {
        val data = MutableLiveData<DateTimeUiModel>()
        data.value = DateTimeUiModel(preference.date, preference.dateHijri, preference.timeZoneId)
        return data
    }
}