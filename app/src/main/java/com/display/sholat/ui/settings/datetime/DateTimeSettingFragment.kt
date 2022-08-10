package com.display.sholat.ui.settings.datetime

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.display.sholat.App
import com.display.sholat.base.BaseFragment
import com.display.sholat.databinding.FragmentDateTimeSettingBinding
import com.display.sholat.databinding.LayoutItemMenuEditBinding
import com.display.sholat.ui.settings.dialoglist.DialogListFragment
import com.display.sholat.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.alhazmy13.hijridatepicker.date.hijri.HijriDatePickerDialog
import java.util.*


/**
 * Created by Jumadi Janjaya date on 29/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class DateTimeSettingFragment : BaseFragment<DateTimeViewModel>(DateTimeViewModel::class) {

    private lateinit var binding: FragmentDateTimeSettingBinding

    private lateinit var uiModel: DateTimeUiModel
    private val list = ArrayList<Pair<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDateTimeSettingBinding.inflate(inflater)
        binding.tvTitle.text = App.string.title_edit_date_and_time
        binding.btnSave.text = App.string.save
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timeZones: Array<String> = TimeZone.getAvailableIDs()
        val timeZoneList = ArrayList<String>()
        Util.getTimeZoneList(OffsetBase.UTC)

        for (i in timeZones.indices) {
            val timeZone = TimeZone.getTimeZone(timeZones[i])
            if (!timeZoneList.contains(TimeZone.getTimeZone(timeZones[i]).displayName)) {
                timeZoneList.add(TimeZone.getTimeZone(timeZones[i]).displayName)
                list.add(Pair(timeZone.id, timeZone.displayName.replace("GMT", "UTC")))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            viewModel.save()
            Toast.makeText(requireContext(), App.string.toast_saved_successfully, Toast.LENGTH_LONG).show()
        }


        binding.dateZone.root.setOnClickListener {
            DialogListFragment(true, App.string.title_select_timezone,list, uiModel.timeZoneId) {

                App.setTimeZoneId(it)
                uiModel = uiModel.copy(timeZoneId = it)
                viewModel.saveTimeZoneIdTmp(it)

                initSetting(true)
        }.show(childFragmentManager, "DialogList")
        }

        binding.dateMasehi.root.setOnClickListener {
            val listDate = Util.listRangeDate(uiModel.dateRange)

            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val ranges = Util.getRange("$year/$month/$day")
                    uiModel = uiModel.copy(dateRange = "${ranges[0]}|${ranges[1]}|${ranges[2]}")
                    viewModel.saveDateRangeTmp(uiModel.dateRange)
                    initSetting(true)
                },
                listDate.year, listDate.month-1, listDate.day
            )
            dialog.show()
        }

        binding.dateHijri.root.setOnClickListener {
            val hijri = Util.listRangeHijri(uiModel.hijriRange)
            Log.d("DateTime", " old $hijri | ${uiModel.hijriRange}")

            Locale.setDefault(Locale.ENGLISH)
            val dpd = HijriDatePickerDialog.newInstance(
                { _, year, month, day ->
                    val date = DateHijri.getLocalDate(DateHijri.Formatter(year = year, month = month, day = day, monthName = "", dayName = ""))
                    val ranges = Util.getRange("${date.year}/${date.monthValue}/${date.dayOfMonth}")
                    uiModel = uiModel.copy(hijriRange = "${ranges[0]}|${ranges[1]}|${ranges[2]-1}")
                    Log.d("DateTime", "$hijri | $date | $year $month $day | ${uiModel.hijriRange}")
                    viewModel.saveHijriRangeTmp(uiModel.hijriRange)
                    App.setLocalDefault()
                    initSetting(false)
                },
                hijri.year, hijri.month-1, hijri.day
            )
            dpd.show(childFragmentManager, "HijriDatePickerDialog")
        }

        viewModel.getUiModel().observe(viewLifecycleOwner) {
            uiModel = it
            initSetting(false)
        }

        CoroutineScope(Dispatchers.Main).launch {
            binding.dateZone.root.let {
                it.isFocusable = true
                //it.requestFocus()
            }
        }
    }

    private fun initSetting(isSetMasehi: Boolean) {
        val timeZone = TimeZone.getTimeZone(uiModel.timeZoneId)
        val listRange = Util.listRangeDate(uiModel.dateRange)
        val date = Util.getDate("dd-MM-yyyy", "${listRange.day}-${listRange.month}-${listRange.year}")
        val hijri = if (isSetMasehi) DateHijri(date!!).writeIslamicDate().also { saveHijriDate() } else {
            if (uiModel.hijriRange == "0-0-0") DateHijri(date!!).writeIslamicDate()
            else Util.listRangeHijri(uiModel.hijriRange)
        }
        val hijriDate = "${hijri.day} ${hijri.monthName}, ${hijri.year} H"

        binding.dateZone.create(App.string.zone_time, "UTC ${timeZone.currentOffset()} hours")
        binding.dateMasehi.create(App.string.date_masehi, Util.dateFormat("dd MMM yyyy", date?.time))
        binding.dateHijri.create(App.string.date_hijri, hijriDate)
    }

    private fun saveHijriDate() {
        uiModel = uiModel.copy(hijriRange = "0-0-0")
        viewModel.saveHijriRangeTmp(uiModel.hijriRange)
        App.setLocalDefault()
    }

    private fun LayoutItemMenuEditBinding.create(name: String, value: String) {
        tvName.text = name
        tvValue.text = value
        root.initializeFocusZoom(1.1f)
    }

}