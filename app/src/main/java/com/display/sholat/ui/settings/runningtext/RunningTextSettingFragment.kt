package com.display.sholat.ui.settings.runningtext

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.display.sholat.App
import com.display.sholat.base.BaseFragment
import com.display.sholat.data.entity.RunningText
import com.display.sholat.data.entity.formatter
import com.display.sholat.databinding.FragmentRunningTextSettingBinding
import com.display.sholat.ui.settings.calendarrange.DialogCalenderRange
import com.display.sholat.ui.settings.dialoglist.DialogListFragment
import com.display.sholat.util.Util
import com.display.sholat.util.readTextFromUri
import java.util.*

/**
 * Created by Jumadi Janjaya date on 30/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class RunningTextSettingFragment : BaseFragment<RunningTextViewModel>(RunningTextViewModel::class) {

    companion object {
        const val READ_REQUEST_CODE = 200
        const val REQUEST_PERMISSION_STORAGE = 300
    }

    private lateinit var binding: FragmentRunningTextSettingBinding
    private val adapter by lazy {
        RunningTextAdapter(
            { initRunningText(it) },
            { deleteItem(it) }, { updateItemEdit(it) }, { editSpeed(it) })
    }

    private val speeds by lazy {
        listOf(
            Pair("5000", App.string.second.formatter("5")),
            Pair("10000", App.string.second.formatter("10")),
            Pair("15000", App.string.second.formatter("15")),
            Pair("20000", App.string.second.formatter("20")),
            Pair("25000", App.string.second.formatter("25")),
            Pair("30000", App.string.second.formatter("30")),
            Pair("40000", App.string.second.formatter("40")),
            Pair("50000", App.string.second.formatter("50")),
            Pair((60 * 1000L).toString(), App.string.minute.formatter("1")),
            Pair((2 * 60 * 1000L).toString(), App.string.minute.formatter("2")),
        )
    }

    private fun editSpeed(it: RunningText) {
        currentRunningText = it
        DialogListFragment(false,
            App.string.speed_runningText,
            speeds,
            it.speed.toString()
        ) { key ->
            val runningText = it.copy(speed = if (Util.isNumber(key)) key.toLong() else 5000)
            if (currentRunningText.id == it.id) currentRunningText = runningText
            viewModel.update(runningText, 2)
            adapter.update(runningText)
        }.show(childFragmentManager, "DialogList")
    }

    private lateinit var currentRunningText: RunningText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunningTextSettingBinding.inflate(inflater)
        binding.rvList.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
        binding.bgPreview.isVisible = false
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = App.string.title_edit_running_text
        binding.btnSchedule.text = App.string.schedule_running_text
        binding.btnSave.text = App.string.save
        binding.btnUpload.text = App.string.upload_text

        viewModel.responseCurrent.observe(viewLifecycleOwner) { runningText ->
            runningText?.let { initRunningText(it) }
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveAll()
            Toast.makeText(requireContext(), App.string.toast_successfully_applied, Toast.LENGTH_LONG).show()
        }

        binding.btnUpload.setOnClickListener { performFileSearch() }

        binding.btnSchedule.setOnClickListener { schedule() }

        viewModel.responseAll.observe(viewLifecycleOwner) {
            Log.d("RunningText", "size ${it.size}")
            adapter.summitList(it)
        }

        viewModel.getAll()
        viewModel.getCurrent()

    }

    private fun schedule() {
        if (this::currentRunningText.isInitialized) currentRunningText.let { runningText ->
            DialogCalenderRange(runningText.scheduleStart, runningText.scheduleEnd) { start, end ->
                currentRunningText = runningText.copy(scheduleStart = start, scheduleEnd = end)
                viewModel.update(currentRunningText, 1)
                adapter.update(currentRunningText)
            }.show(childFragmentManager, "DialogCalenderRange")
        }
    }

    private fun initRunningText(runningText: RunningText) = with(runningText) {
        currentRunningText = this
        binding.tvRunningText.text = text
        val animation = TranslateAnimation(1500f, -1500f, 0f, 0f)
        animation.duration = speed
        animation.repeatMode = Animation.RESTART
        animation.repeatCount = Animation.INFINITE
        animation.interpolator = LinearInterpolator()
        binding.tvRunningText.startAnimation(animation)
    }

    private fun performFileSearch() {
        val intent = Intent()
        intent.type = "text/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select text"),
            READ_REQUEST_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data
                if (uri != null) {
                    val text = requireActivity().readTextFromUri(uri)
                    import(text)
                }
            }
        }
    }

    private fun import(text: String) {
        val texts = text.split("#")
        if (text.isNotEmpty()) {
            val runningText = when (texts.size) {
                2 -> RunningText(
                    0,
                    texts[1],
                    Util.getDate("dd/MM/yyyy", texts[0], TimeZone.getDefault())!!.time,
                    Util.getDate("dd/MM/yyyy", texts[0], TimeZone.getDefault())!!.time,
                    10000L
                )
                3 -> RunningText(
                    0,
                    texts[2],
                    Util.getDate("dd/MM/yyyy", texts[0], TimeZone.getDefault())!!.time,
                    Util.getDate("dd/MM/yyyy", texts[1], TimeZone.getDefault())!!.time,
                    10000L
                )
                4 -> RunningText(
                    0,
                    texts[3],
                    Util.getDate("dd/MM/yyyy", texts[0], TimeZone.getDefault())!!.time,
                    Util.getDate("dd/MM/yyyy", texts[1], TimeZone.getDefault())!!.time,
                    if (Util.isNumber(texts[2])) texts[2].toInt() * 1000L else 10000L
                )
                else -> RunningText(0, text, 0L, 0L, 10000L)
            }
            val id = adapter.add(runningText)
            viewModel.insert(runningText.copy(id = id))
            binding.rvList.smoothScrollToPosition(adapter.itemCount - 1)
        } else {
            Toast.makeText(requireContext(), "Text empty", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteItem(runningText: RunningText) {
        viewModel.delete(runningText)
        adapter.delete(runningText)
    }

    private fun updateItemEdit(runningText: RunningText) {
        currentRunningText = runningText
        //if (currentRunningText.id == runningText.id) currentRunningText = runningText
        viewModel.update(runningText, 0)
        //adapter.update(runningText)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) performFileSearch()
            else Toast.makeText(requireContext(), "Permission not granted!", Toast.LENGTH_LONG)
                .show()
        }
    }

}