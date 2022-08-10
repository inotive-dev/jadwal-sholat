package com.display.sholat.ui.settings.language

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.display.sholat.App
import com.display.sholat.data.Preference
import com.display.sholat.data.entity.Strings
import com.display.sholat.data.repository.PrayerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LangViewModel @Inject constructor(private val preference: Preference, private val repository: PrayerRepository) : ViewModel() {

    private val _lang = MutableLiveData<String>()

    val responseLangStrings = Transformations.switchMap(_lang) { repository.getLangStrings(it) }


//    fun getCountyList() = repository.getLangListv2()
    fun getLanguageList() = repository.getLangListv2()

    fun getLanguage() : LiveData<String> {

        Log.e("xlogx","cuy " + responseLangStrings)

        val data = MutableLiveData<String>()
        data.value = preference.langId
        Log.e("xlogx","getLanguage B")
        return data
    }

    fun saveStrings(strings: Strings) = viewModelScope.launch {
        preference.langId = _lang.value!!
        val strings2 = Strings.copy(App.stringDefault, strings)
        preference.language = Gson().toJson(strings)
        App.setLang(_lang.value!!, strings2)
    }

    fun getLangStrings(langId: String) = viewModelScope.launch {
        _lang.value = langId
    }

    fun saveLangDisplayName(langDisplayName: String) {
        preference.language = langDisplayName
    }

    fun clear() = viewModelScope.launch {
        //waktu sholat tidak berubah
//        repository.save(Prayer("00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00"))
//        preference.iqomahNow = Iqomah("00:00", "00:00", "00:00", "00:00", "00:00", "00:00")
//        preference.prayerCity = ""
    }
}