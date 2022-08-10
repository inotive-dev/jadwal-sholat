package com.display.sholat.ui.settings.prayer

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.display.sholat.App
import com.display.sholat.R
import com.display.sholat.base.BaseFragment
import com.display.sholat.data.entity.Iqomah
import com.display.sholat.data.entity.Prayer
import com.display.sholat.data.repository.Result
import com.display.sholat.databinding.FragmentPrayerSettingBinding
import com.display.sholat.databinding.LayoutItemMenuEdit2Binding
import com.display.sholat.databinding.LayoutItemMenuEditBinding
import com.display.sholat.databinding.LayoutNumberPickerBinding
import com.display.sholat.ui.settings.dialoglist.DialogListFragment
import com.display.sholat.util.initializeFocusZoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jumadi Janjaya date on 30/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/


class PrayerSettingFragment : BaseFragment<PrayerViewModel>(PrayerViewModel::class) {

    private lateinit var binding: FragmentPrayerSettingBinding
    private lateinit var prayer: Prayer
    private lateinit var iqomah: Iqomah
    private lateinit var location: String
    private lateinit var selectedCountry: String

    private val countries = ArrayList<String>()
    private val locations = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerSettingBinding.inflate(inflater)
        binding.tvTitle.text = App.string.title_edit_schedule_prayer
        binding.btnUpdate.text = App.string.update
        binding.btnRefresh.text = App.string.refresh_data
        binding.txtPrayer.text = App.string.time_prayer
        binding.txtIqomah.text = App.string.iqomah
        binding.txtSunnah.text = App.string.sunnah_fasting
        binding.lokasi.tvName.text = App.string.location
        binding.lokasi.tvValue.text = App.string.title_select_location

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getCountries()

        binding.btnRefresh.setOnClickListener {
            if (this::location.isInitialized) {
                DialogSyncFragment(location, prayer) {
                    initPlayer(it, iqomah)
                }.show(childFragmentManager, "DialogSync")
            } else {
                Toast.makeText(requireContext(), "is loading location data", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnUpdate.setOnClickListener {
            if (this::prayer.isInitialized && this::iqomah.isInitialized && this::location.isInitialized) {
                viewModel.save(prayer, iqomah, location)
                Toast.makeText(requireContext(), App.string.toast_successfully_updated, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), App.string.toast_nothing_is_updated, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.responseCountries.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Error -> Toast.makeText(requireContext(), it.t.message?:"Not connect internet", Toast.LENGTH_LONG).show()
                Result.Loading -> {
                }
                is Result.Success -> {
                    countries.clear()
                    countries.addAll(it.data)
                    initCountryLocation()
                }
            }
        }

        viewModel.responseCities.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Error -> Toast.makeText(requireContext(), it.t.message?:"Not connect internet", Toast.LENGTH_LONG).show()
                Result.Loading -> {
                }
                is Result.Success -> {
                    locations.clear()
                    locations.addAll(it.data)
                    if(locations.size > 0) {
                        initLocation(locations.get(0))
                    }
                }
            }
        }

        viewModel.getPlayer().observe(viewLifecycleOwner) { prayer ->
            viewModel.getIqomah().observe(viewLifecycleOwner) {
                initPlayer(prayer, it)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            binding.lokasiNegara.root.let {
                it.isFocusable = true
                //it.requestFocus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        if (locations.isEmpty()) viewModel.getCountries()
    }

    private fun initCountryLocation() = binding.lokasiNegara.apply {
        tvName.text = App.string.country
        tvValue.text = if (viewModel.currentCountry().isEmpty()) App.string.title_select_location else viewModel.currentCountry()

        viewModel.getCitiesFromCountry()
        root.setOnClickListener {

            if (countries.isNotEmpty()) {
                val list = countries.map { Pair(it, it) }

                DialogListFragment(true, App.string.title_select_location, list, "") { result ->
                    selectedCountry = result
                    tvValue.text = result
                    viewModel.saveCountry(selectedCountry)
                    viewModel.getCitiesFromCountry()
                }.show(childFragmentManager, "DialogListFragment")
            }
        }
    }

    private fun initLocation(city: String) = binding.lokasi.apply {
        tvName.text = App.string.city

        tvValue.text = if (viewModel.currentCity().isEmpty()) App.string.title_select_location else viewModel.currentCity()
        location = if (viewModel.currentCity().isEmpty()) App.string.title_select_location else viewModel.currentCity()

        root.setOnClickListener {

            if (locations.isNotEmpty()) {
                val list = locations.map { Pair(it, it) }
                DialogListFragment(true, App.string.title_select_location, list, city.lowercase()) { result ->
                    location = result
                    tvValue.text = result
                }.show(childFragmentManager, "DialogListFragment")
            }
        }
    }

    private fun initPlayer(pl: Prayer, iq: Iqomah) {
        prayer = pl
        iqomah = iq
        binding.syuruq.create(App.string.syuruq, prayer.syuruq, "", { bin ->
            timePicker(prayer.syuruq) {
                bin.tvValue.text = it
                prayer = prayer.copy(syuruq = it)
            }
        }, {}).btnIqomah.visibility = View.INVISIBLE
        binding.shubu.create(App.string.fajr, prayer.subuh, iqomah.subuh, { bin ->
            timePicker(prayer.subuh) {
                bin.tvValue.text = it
                prayer = prayer.copy(subuh = it)
            }
        }, { bin ->
            numberPicker {
                val iqTime = bin.tvValue.text.toString().addMinutes(it)
                bin.tvName2.text = iqTime
                iqomah = iqomah.copy(subuh = iqTime)
            }
        })
        binding.zhuhur.create(App.string.dhuhr, prayer.dzuhur, iqomah.dzuhur, { bin ->
            timePicker(prayer.dzuhur) {
                bin.tvValue.text = it
                prayer = prayer.copy(dzuhur = it)
            }
        },  { bin ->
            numberPicker {
                val iqTime = bin.tvValue.text.toString().addMinutes(it)
                bin.tvName2.text = iqTime
                iqomah = iqomah.copy(dzuhur = iqTime)
            }
        })
        binding.ashar.create(App.string.asr, prayer.ashar, iqomah.ashar, { bin ->
            timePicker(prayer.ashar) {
                bin.tvValue.text = it
                prayer = prayer.copy(ashar = it)
            }
        }, { bin ->
            numberPicker {
                val iqTime = bin.tvValue.text.toString().addMinutes(it)
                bin.tvName2.text = iqTime
                iqomah = iqomah.copy(ashar = iqTime)
            }
        })
        binding.magrib.create(App.string.maghrib, prayer.maghrib, iqomah.maghrib, { bin ->
            timePicker(prayer.maghrib) {
                bin.tvValue.text = it
                prayer = prayer.copy(maghrib = it)
            }
        }, { bin ->
            numberPicker {
                val iqTime = bin.tvValue.text.toString().addMinutes(it)
                bin.tvName2.text = iqTime
                iqomah = iqomah.copy(maghrib = iqTime)
            }
        }).also {
            it.btnSunnah.isVisible = true
            it.tvName3.text = iqomah.maghrib2
            it.tvName3.setOnClickListener { v ->
                numberPicker { minute ->
                    val iqTime = it.tvValue.text.toString().addMinutes(minute)
                    it.tvName3.text = iqTime
                    iqomah = iqomah.copy(maghrib2 = iqTime)
                }
            }
        }

        binding.isha.create(App.string.isha, prayer.isya, iqomah.isya, { bin ->
            timePicker(prayer.isya) {
                bin.tvValue.text = it
                prayer = prayer.copy(isya = it)
            }
        }, { bin ->
            numberPicker {
                val iqTime = bin.tvValue.text.toString().addMinutes(it)
                bin.tvName2.text = iqTime
                iqomah = iqomah.copy(isya = iqTime)
            }
        })
    }

    private fun timePicker(current: String, callback: (time: String) -> Unit) {
        val txt = current.split(":")
        val hour = if (txt.size == 2) txt[0].toInt() else 23
        val minute = if (txt.size == 2) txt[1].toInt() else 30

        TimePickerDialog(requireContext(),
            { _, p1, p2 ->
                val time = "${p1.toStringFormat()}:${p2.toStringFormat()}"
                Log.d("Prayer", time)
                callback.invoke(time)
            },
            hour, minute, true) .show()
    }

    private fun numberPicker(callback: (time: Int) -> Unit){
        val dialog = Dialog(requireContext())
        val dialogBinding = LayoutNumberPickerBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)

        with(dialogBinding){
            with(numberPicker){
                minValue = 1
                maxValue = 60
            }

            btnOk.setOnClickListener {
                callback.invoke(numberPicker.value)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun String.addMinutes(minutes : Int) : String{
        val sdf = SimpleDateFormat("HH:mm")
        val time = sdf.parse(this)
        val cal = Calendar.getInstance()
        cal.time = time
        cal.add(Calendar.MINUTE, minutes)
        return sdf.format(cal.time)
    }


    private fun Int.toStringFormat() : String {
        return if ("$this".toCharArray().size > 1) "$this" else "0$this"
    }

    private fun LayoutItemMenuEdit2Binding.create(nameId: String, value: String, value2: String, click: (bin: LayoutItemMenuEdit2Binding) -> Unit, click2: (bin: LayoutItemMenuEdit2Binding) -> Unit) : LayoutItemMenuEdit2Binding {
        binding.root.setBackgroundResource(R.drawable.bg_round_alpha)
        tvName.text = nameId
        tvValue.text = value
        tvName2.text = value2
        btnPrayer.setOnClickListener {
            click.invoke(this)
        }
        btnIqomah.setOnClickListener { click2.invoke(this) }
        return this
    }

    private fun LayoutItemMenuEditBinding.create(name: String, value: String) {
        tvName.text = name
        tvValue.text = value
        root.initializeFocusZoom(1.1f)
    }
}