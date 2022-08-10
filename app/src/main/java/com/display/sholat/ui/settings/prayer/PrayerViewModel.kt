package com.display.sholat.ui.settings.prayer

import androidx.lifecycle.*
import com.display.sholat.data.Preference
import com.display.sholat.data.entity.Iqomah
import com.display.sholat.data.entity.Prayer
import com.display.sholat.data.repository.PrayerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class PrayerViewModel @Inject constructor(
    private val preference: Preference,
    private val prayerRepository: PrayerRepository) : ViewModel() {

    private val _countries = MutableLiveData<String>()
    private val _cities = MutableLiveData<String>()

    val responseCountries = Transformations.switchMap(_countries) {
        prayerRepository.getListCountry()
    }

    val responseCities = Transformations.switchMap(_cities) {
        //it malaysia indoneia
        prayerRepository.getCities(it)
    }

    fun getCitiesFromCountry() = viewModelScope.launch {
        _cities.value = preference.countryDisplayName
    }

    fun getCountries() = viewModelScope.launch {
        _countries.value = null
    }

    fun getPlayer() = prayerRepository.getPrayerWithLocal()

    fun updatePlayer(city: String) = prayerRepository.getPlayerV2(city)

    fun getIqomah() : LiveData<Iqomah> {
        val data = MutableLiveData<Iqomah>()
        data.value = preference.iqomahNow
        return data
    }

    fun saveCountry(country: String) = viewModelScope.launch {
        preference.countryDisplayName = country
        preference.prayerCity = ""
    }

    fun save(prayer: Prayer, iqomah: Iqomah, city: String) = viewModelScope.launch {
        prayerRepository.save(prayer)
        preference.iqomahNow = iqomah
        preference.prayerCity = city
    }

    fun currentCity() = preference.prayerCity

    fun currentCountry() = preference.countryDisplayName
}