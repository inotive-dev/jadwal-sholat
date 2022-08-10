package com.display.sholat.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.display.sholat.App
import com.display.sholat.data.Preference
import com.display.sholat.data.api.ApiPrayerService
import com.display.sholat.data.entity.*
import com.display.sholat.util.Util
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class PrayerRepository @Inject constructor(
    private val application: Application,
    private val api: ApiPrayerService,
    private val preference: Preference) {

    fun getPlayer() : LiveData<Result<PrayerResponse>> {
        val data = MutableLiveData<Result<PrayerResponse>>()
        data.value = Result.Loading
        val old = Locale.getDefault()
        Locale.setDefault(Locale.ROOT)
        val format = Util.dateFormat("yyyy-MM-hh", App.getDate().time)
        Locale.setDefault(old)

        api.prayerNow(preference.prayerCity, format).enqueue(object : Callback<PrayerResponse> {
            override fun onResponse(
                call: Call<PrayerResponse>,
                response: Response<PrayerResponse>
            ) {
                if (response.body() != null) data.value = Result.Success(response.body()!!)
                else data.value = Result.Error(Throwable(response.message()))
            }

            override fun onFailure(call: Call<PrayerResponse>, t: Throwable) {
                data.value = Result.Error(t)
            }
        })

        return data
    }

    fun getPlayerV2(city: String? = null) : LiveData<Result<PrayerResponse>> {
        val data = MutableLiveData<Result<PrayerResponse>>()
        data.value = Result.Loading

        api.prayerNowV2(city?:preference.prayerCity.lowercase(), preference.countryDisplayName.lowercase()).enqueue(object : Callback<PrayerResponse> {
            override fun onResponse(
                call: Call<PrayerResponse>,
                response: Response<PrayerResponse>
            ) {
                if (response.body() != null) data.value = Result.Success(response.body()!!)
                else data.value = Result.Error(Throwable(response.message()))
            }

            override fun onFailure(call: Call<PrayerResponse>, t: Throwable) {
                data.value = Result.Error(t)
            }
        })

        return data
    }


    fun getPrayerWithLocal() : LiveData<Prayer> {
        val data = MutableLiveData<Prayer>()
        data.value = preference.prayerNow
        return data
    }

    fun save(prayer: Prayer) {
        preference.prayerNow = prayer
    }

    fun getLocations() : LiveData<Result<List<City>>> {
        val data = MutableLiveData<Result<List<City>>>()
        data.value = Result.Loading

        api.locations().enqueue(object : Callback<List<City>> {
            override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                if (response.body() != null) data.value = Result.Success(response.body()!!)
                else data.value = Result.Error(Throwable(response.message()))
            }

            override fun onFailure(call: Call<List<City>>, t: Throwable) {
                data.value = Result.Error(t)
            }

        })

        return data
    }

    fun getCities(country: String) : LiveData<Result<List<String>>> {
        val data = MutableLiveData<Result<List<String>>>()
        data.value = Result.Loading

        api.listCity(LangRequest(country)).enqueue(object : Callback<DataResponse<List<String>>> {
            override fun onResponse(
                call: Call<DataResponse<List<String>>>,
                response: Response<DataResponse<List<String>>>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.data != null) data.value = Result.Success(response.body()!!.data!!)
                    else data.value = Result.Error(Throwable(response.body()!!.msg))
                }
                else data.value = Result.Error(Throwable(response.message()))
            }

            override fun onFailure(call: Call<DataResponse<List<String>>>, t: Throwable) {
                data.value = Result.Error(t)
            }
        })

        return data
    }

    fun getListCountry() : LiveData<Result<List<String>>> {
        val data = MutableLiveData<Result<List<String>>>()
        data.value = Result.Loading

        api.listCountryV2().enqueue(object : Callback<DataResponse<List<Country>>> {
            override fun onResponse(
                call: Call<DataResponse<List<Country>>>,
                response: Response<DataResponse<List<Country>>>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.data != null) {
                        val countryName:List<String> = response.body()!!.data!!.map { it.name }
                        data.value = Result.Success(countryName)
                    }
                    else data.value = Result.Error(Throwable(response.body()!!.msg))
                }
                else data.value = Result.Error(Throwable(response.message()))
            }

            override fun onFailure(call: Call<DataResponse<List<Country>>>, t: Throwable) {
                data.value = Result.Error(t)
            }
        })

        return data
    }

    fun getListLanguage() : LiveData<Result<List<String>>> {
        val data = MutableLiveData<Result<List<String>>>()
        data.value = Result.Loading

        api.listCountryV2().enqueue(object : Callback<DataResponse<List<Country>>> {
            override fun onResponse(
                call: Call<DataResponse<List<Country>>>,
                response: Response<DataResponse<List<Country>>>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.data != null) {
                        val countryName:List<String> = response.body()!!.data!!.map { it.name }
                        data.value = Result.Success(countryName)
                    }
                    else data.value = Result.Error(Throwable(response.body()!!.msg))
                }
                else data.value = Result.Error(Throwable(response.message()))
            }

            override fun onFailure(call: Call<DataResponse<List<Country>>>, t: Throwable) {
                data.value = Result.Error(t)
            }
        })

        return data
    }

    fun getLangList() : LiveData<Result<List<Lang>>> {
        val data = MutableLiveData<Result<List<Lang>>>()
        data.value = Result.Loading

        api.listCountry().enqueue(object : Callback<List<Lang>> {
            override fun onFailure(call: Call<List<Lang>>, t: Throwable) {
                data.value = Result.Error(t)
            }

            override fun onResponse(call: Call<List<Lang>>, response: Response<List<Lang>>) {
                if (response.body() != null) data.value = Result.Success(response.body()!!)
                else data.value = Result.Error(Throwable("empty data"))
            }
        })

        return data
    }

    fun getLangListv2() : LiveData<Result<List<Language>>> {
        val data = MutableLiveData<Result<List<Language>>>()
        data.value = Result.Loading

        api.listLanguage().enqueue(object : Callback<List<Language>> {
            override fun onFailure(call: Call<List<Language>>, t: Throwable) {
                data.value = Result.Error(t)
            }

            override fun onResponse(call: Call<List<Language>>, response: Response<List<Language>>) {
                if (response.body() != null) data.value = Result.Success(response.body()!!)
                else data.value = Result.Error(Throwable("empty data"))
            }
        })

        return data
    }

    fun getLangStrings(code: String) : LiveData<Result<Strings>> {
        val data = MutableLiveData<Result<Strings>>()
        data.value = Result.Loading
        val string = Gson().toJson(App.stringDefault.copy(translate_to = code))
        if (code == "in" || code == "id") {
            data.value = Result.Success(App.stringDefault)
        } else if (code == "en") {
            data.value = Result.Success(Strings.getDefaultRaw(application))
        } else {
            api.strings(string.toRequestBody("application/json".toMediaType())).enqueue(object : Callback<Strings> {
                override fun onResponse(call: Call<Strings>, response: Response<Strings>) {
                    if (response.body() != null) {
                        data.value = Result.Success(response.body()!!)
                    }
                    else data.value = Result.Error(Throwable(response.message()))
                }

                override fun onFailure(call: Call<Strings>, t: Throwable) {
                    data.value = Result.Error(t)
                }

            })
        }

        return data

    }
}