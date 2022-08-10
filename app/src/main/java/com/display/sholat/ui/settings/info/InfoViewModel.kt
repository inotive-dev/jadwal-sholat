package com.display.sholat.ui.settings.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.display.sholat.data.Preference
import kotlinx.coroutines.launch
import javax.inject.Inject

class InfoViewModel @Inject constructor(private val preference: Preference) : ViewModel() {

    fun save(infoUiModel: InfoUiModel) = viewModelScope.launch {
        preference.nameMosque = infoUiModel.nameMosque
        preference.addressMosque = infoUiModel.addressMosque
    }


    fun getUiModel() : LiveData<InfoUiModel> {
        val data = MutableLiveData<InfoUiModel>()
        data.value = InfoUiModel(preference.nameMosque?:"", preference.addressMosque?:"")
        return data
    }
}