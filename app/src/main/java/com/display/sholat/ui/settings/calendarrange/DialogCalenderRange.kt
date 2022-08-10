package com.display.sholat.ui.settings.calendarrange

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.display.sholat.App
import com.display.sholat.databinding.FragmentDatePickerRangeBinding
import com.display.sholat.util.initializeFocusZoom
import java.util.*


class DialogCalenderRange(private var dateStart: Long, private var dateEnd: Long, private val callback: (start: Long, end: Long) -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentDatePickerRangeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDatePickerRangeBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.textTitle.text = App.string.title_schedule_date
        binding.btnCancel.text = App.string.cancel
        binding.btnOk.text = App.string.set
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.initializeFocusZoom()
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener {
            callback.invoke(dateStart, dateEnd)
            dismiss()
        }

        if (dateStart != 0L && dateEnd != 0L) {
            val startSelectionDate = Calendar.getInstance()
            startSelectionDate.time = Date(dateStart)
            val endSelectionDate = startSelectionDate.clone() as Calendar
            endSelectionDate.time =Date(dateEnd)
            binding.calendar.setSelectedDateRange(startSelectionDate, endSelectionDate)
        }

        binding.calendar.setCalendarListener(object : CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {}

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                dateStart = startDate.time.time
                dateEnd = endDate.time.time
            }
        })
    }

}