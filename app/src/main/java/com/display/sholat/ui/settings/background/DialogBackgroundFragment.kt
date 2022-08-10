package com.display.sholat.ui.settings.background

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.display.sholat.App
import com.display.sholat.R
import com.display.sholat.data.entity.*
import com.display.sholat.databinding.FragmentDialogBackgroundBinding
import com.display.sholat.databinding.LayoutItemMenu2Binding
import com.display.sholat.ui.settings.calendarrange.DialogCalenderRange
import com.display.sholat.ui.settings.dialoglist.DialogListFragment
import com.display.sholat.util.Util
import com.display.sholat.util.initializeFocusZoom

class DialogBackgroundFragment(
    private var uiModel: SlideShowUiModel = SlideShowUiModel(Uri.EMPTY),
    private val mode: Int = 0,
    private val callback: (uiModel: SlideShowUiModel) -> Unit) : DialogFragment() {

    companion object {
        const val READ_REQUEST_CODE = 200
        const val REQUEST_PERMISSION_STORAGE = 300
    }

    private lateinit var binding: FragmentDialogBackgroundBinding
    private val prayers by lazy { listOf(
        Pair("not-set", getString(R.string.not_set)),
        Pair(KEY_IMSAK, App.string.imsak),
        Pair(KEY_SUBUH, App.string.fajr),
        Pair(KEY_DZUHUR, App.string.dhuhr),
        Pair(KEY_ASHAR, App.string.asr),
        Pair(KEY_MAGHRIB, App.string.maghrib),
        Pair(KEY_ISYA, App.string.isha)) }

    private val durations by lazy { listOf(
            Pair("5000", App.string.second.formatter("5")),
            Pair("10000", App.string.second.formatter("10")),
            Pair("15000", App.string.second.formatter("15")),
            Pair("20000", App.string.second.formatter("20")),
            Pair("25000", App.string.second.formatter("25")),
            Pair("30000", App.string.second.formatter("30")),
            Pair("40000", App.string.second.formatter("40")),
            Pair("50000", App.string.second.formatter("50")),
            Pair((60 * 1000).toString(), App.string.minute.formatter("1")),
            Pair((2 * 60 * 1000).toString(), App.string.minute.formatter("2")),
            Pair((3 * 60 * 1000).toString(), App.string.minute.formatter("3")),
            Pair((4 * 60 * 1000).toString(), App.string.minute.formatter("4")),
            Pair((5 * 60 * 1000).toString(), App.string.minute.formatter("5")),
            Pair((6 * 60 * 1000).toString(), App.string.minute.formatter("6")),
            Pair((7 * 60 * 1000).toString(), App.string.minute.formatter("7")),
            Pair((8 * 60 * 1000).toString(), App.string.minute.formatter("8")),
            Pair((9 * 60 * 1000).toString(), App.string.minute.formatter("9")),
            Pair((10 * 60 * 1000).toString(), App.string.minute.formatter("10")),
            Pair((15 * 60 * 1000).toString(), App.string.minute.formatter("15")),
            Pair((20 * 60 * 1000).toString(), App.string.minute.formatter("20")),
            Pair((30 * 60 * 1000).toString(), App.string.minute.formatter("30")),
            Pair((50 * 60 * 1000).toString(), App.string.minute.formatter("50")),
        ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogBackgroundBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.title.text = App.string.title_add_slideshow
        binding.btnOk.text = App.string.set
        binding.cbFullscreen.text = App.string.full_background
        binding.cbShowclock.text = App.string.showclock
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.initializeFocusZoom()
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener {
            callback.invoke(uiModel.copy(isFullScreen = binding.cbFullscreen.isChecked))
            callback.invoke(uiModel.copy(showClock = binding.cbShowclock.isChecked))
            dismiss()
        }

        initMenu()
        binding.prayer.root.isVisible = false


        if (mode == 1) {
            binding.upload.root.isVisible = false
            binding.cbFullscreen.isChecked = uiModel.isFullScreen
            binding.cbShowclock.isChecked = uiModel.showClock
            binding.title.text = App.string.title_edit_slideshow
            binding.prayer.tvName.text = prayers.findLast { it.first == uiModel.prayer }?.second?: App.string.select_prayer
            binding.duration.tvName.text = App.string.duration.formatter(durations.findLast { it.first == "${uiModel.duration}" }?.second)

            binding.scheduleTime.tvName.text = if (uiModel.scheduleTimeStart.isNotEmpty() && uiModel.scheduleTimeEnd.isNotEmpty())
                uiModel.scheduleTimeStart +" - " + uiModel.scheduleTimeEnd
            else App.string.schedule_time

            binding.schedule.tvName.text = if (uiModel.scheduleStart != 0L && uiModel.scheduleEnd != 0L)
                Util.dateFormat("yyyy/MM/dd", uiModel.scheduleStart) + " - " + Util.dateFormat("yyyy/MM/dd", uiModel.scheduleEnd)
             else
                App.string.schedule_slideshow
        } else if (mode == 2) {
            binding.title.text = App.string.change_background
            binding.cbFullscreen.isVisible = false
            binding.cbShowclock.isVisible = false
            binding.scheduleTime.root.isVisible = false
            binding.prayer.root.isVisible = false
            binding.duration.root.isVisible = false
            binding.schedule.root.isVisible = false
        }
    }

    private fun initMenu() {
        binding.upload.create(App.string.upload_file, R.drawable.ic_upload) { performFileSearch() }
        binding.prayer.create(App.string.select_prayer, R.drawable.ic_calendar_check_line) {

            DialogListFragment(false, App.string.select_prayer, prayers, uiModel.prayer) { key ->
                if (key != "not-set") {
                    uiModel = uiModel.copy(prayer = key)
                    it.tvName.text = prayers.findLast { it.first == uiModel.prayer }?.second
                } else {
                    uiModel = uiModel.copy(prayer = "")
                    it.tvName.text = App.string.select_prayer
                }
            }.show(childFragmentManager, "DialogList")

        }
        binding.duration.create(App.string.duration_slideshow, R.drawable.ic_timer) {
            DialogListFragment(false, App.string.duration_slideshow, durations, uiModel.duration.toString()) { key ->
                uiModel = uiModel.copy(duration = key.toLong())
                it.tvName.text = App.string.duration.formatter(durations.findLast { it.first == key }?.second)
            }.show(childFragmentManager, "DialogList")
        }

        binding.schedule.create(App.string.schedule_slideshow, R.drawable.ic_calendar_event_fill) {
            DialogCalenderRange(uiModel.scheduleStart, uiModel.scheduleEnd) { start, end ->
                uiModel = uiModel.copy(scheduleStart = start, scheduleEnd = end)
                val string = Util.dateFormat("yyyy/MM/dd", start) + " - " + Util.dateFormat("yyyy/MM/dd", end)
                it.tvName.text = string
            }.show(childFragmentManager, "DialogCalenderRange")

        }

        binding.scheduleTime.create(App.string.schedule_time, R.drawable.ic_baseline_access_time_24) { bin ->
            val hourStart = uiModel.scheduleTimeStart.split(":").run { if (this.size == 2) this[0] else "6" }.toInt()
            val minuteStart = uiModel.scheduleTimeStart.split(":").run { if (this.size == 2) this[1] else "10" }.toInt()
            val hourEnd = uiModel.scheduleTimeEnd.split(":").run { if (this.size == 2) this[0] else "6" }.toInt()
            val minuteEnd = uiModel.scheduleTimeEnd.split(":").run { if (this.size == 2) this[1] else "10" }.toInt()

            val pickerStart = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(hourStart)
                    .setMinute(minuteStart)
                .setTitleText(App.string.title_select_time_start)
                    .build()

            pickerStart.addOnPositiveButtonClickListener {
                val timeStart = "${pickerStart.hour}:${pickerStart.minute}"
                Log.e("Dialog", timeStart)
                uiModel = uiModel.copy(scheduleTimeStart = timeStart)

                val pickerEnd = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(hourEnd)
                    .setMinute(minuteEnd)
                    .setTitleText(App.string.title_select_time_end)
                    .build()

                pickerEnd.addOnPositiveButtonClickListener {
                    val timeEnd = "${pickerEnd.hour}:${pickerEnd.minute}"
                    uiModel = uiModel.copy(scheduleTimeEnd = timeEnd)
                    val string = "$timeStart - $timeEnd"
                    Log.e("Dialog", string)
                    bin.tvName.text = string
                }
                pickerEnd.show(childFragmentManager, "TimePickerEnd")
            }
            pickerStart.show(childFragmentManager, "TimePickerStart")
        }
    }

    private fun performFileSearch() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(intent, "Select Image/Video"),
            READ_REQUEST_CODE
        )
    }

    private fun LayoutItemMenu2Binding.create( nameId: String, @DrawableRes icon: Int, callback: (binding: LayoutItemMenu2Binding) -> Unit) {
        root.initializeFocusZoom(1.1f)
        tvName.text = nameId
        imgIcon.setImageResource(icon)
        root.setOnClickListener {callback.invoke(this)}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data
                if (uri != null) {
                    uiModel = uiModel.copy(uri = uri)
                    binding.upload.tvName.text = Util.queryName(requireActivity().contentResolver, uri)
                }
            }
        }
    }

}