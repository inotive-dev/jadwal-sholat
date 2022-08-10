package com.display.sholat.ui.settings.background

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.display.sholat.App
import com.display.sholat.base.BaseFragment
import com.display.sholat.data.entity.SlideShow
import com.display.sholat.databinding.FragmentBackgroundSettingBinding
import com.display.sholat.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Created by Jumadi Janjaya date on 29/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class BackgroundSettingFragment : BaseFragment<SlideViewModel>(SlideViewModel::class) {

    private lateinit var binding: FragmentBackgroundSettingBinding
    private val adapter = BackgroundAdapter {
        binding.cbFullscreen.tag = it
        binding.cbFullscreen.isChecked = it.isFullScreen
        binding.cbShowclock.tag = it
        binding.cbShowclock.isChecked = it.showClock
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBackgroundSettingBinding.inflate(inflater)
        binding.tvTitle.text = App.string.title_edit_background
        binding.btnDelete.text = App.string.delete
        binding.btnEdit.text = App.string.edit
        binding.btnAddSlide.text = App.string.add_slideshow
        binding.btnSave.text = App.string.save
        binding.cbFullscreen.text = App.string.full_background
        binding.cbShowclock.text = App.string.showclock
        binding.tvDesc.text = App.string.title_info

        binding.rvList.let {
            it.layoutManager = GridLayoutManager(requireContext(), 4)
            it.adapter = adapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEdit.setOnClickListener {
            if (adapter.selected != null) {
                val slide = adapter.find(adapter.selected!!.first)
                if (slide != null) {
                    DialogBackgroundFragment(slide.toSlideShowUiModel(), 1) {
                        val newSlide = slide.copy(
                            duration = it.duration,
                            prayer = it.prayer,
                            scheduleStart = it.scheduleStart,
                            scheduleEnd = it.scheduleEnd,
                            scheduleTimeStart = it.scheduleTimeStart,
                            scheduleTimeEnd = it.scheduleTimeEnd
                        )
                        viewModel.update(newSlide)
                        adapter.update(newSlide)
                    }.show(childFragmentManager, "DialogBackground")
                } else {
                    Toast.makeText(
                        requireContext(),
                        App.string.toast_nothing_selected,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            if (adapter.selected != null) {
                val slide = adapter.find(adapter.selected!!.first)?.let {
                    viewModel.delete(it)
                    adapter.delete(it)
                }
                if (slide == null)
                    Toast.makeText(
                        requireContext(),
                        App.string.toast_nothing_selected,
                        Toast.LENGTH_LONG
                    ).show()
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveAll()
            Toast.makeText(
                requireContext(),
                App.string.toast_successfully_applied,
                Toast.LENGTH_LONG
            ).show()
        }

        binding.btnAddSlide.setOnClickListener {
            DialogBackgroundFragment {
                CoroutineScope(Dispatchers.IO).launch {
                    handleAddSlide(it)
                }
            }.show(childFragmentManager, "DialogBackground")
        }

        binding.cbFullscreen.setOnCheckedChangeListener { checkboxView, isChecked ->
            if (checkboxView.tag is SlideShow) {
                val item = checkboxView.tag as SlideShow
                if (isChecked != item.isFullScreen) {
                    viewModel.update(item.copy(isFullScreen = isChecked))
                    adapter.update(item.copy(isFullScreen = isChecked))
                }
            }
        }

        binding.cbShowclock.setOnCheckedChangeListener { checkboxView, isChecked ->
            if (checkboxView.tag is SlideShow) {
                val item = checkboxView.tag as SlideShow
                if (isChecked != item.showClock) {
                    viewModel.update(item.copy(showClock = isChecked))
                    adapter.update(item.copy(showClock = isChecked))
                }
            }
        }

        viewModel.responseSlideShows.observe(viewLifecycleOwner) {
            adapter.summitList(it)
        }

        viewModel.getAll()
    }

    private fun handleAddSlide(uiModel: SlideShowUiModel) {
        copyFile(uiModel.uri, Date().time.toString()) { filename, type ->
            val data = uiModel.toSlideShow(0, type).copy(fileName = filename)
            viewModel.save(data)
            CoroutineScope(Dispatchers.Main).launch {
                adapter.add(data)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(
        uri: Uri,
        filename: String,
        output: (filename: String, type: String) -> Unit
    ) {
        val contentResolver = requireActivity().contentResolver
        val type = if ((contentResolver.getType(uri) ?: "").contains("image")) "image" else "video"

        val dir = File(
            requireContext().getExternalFilesDir(if (type == "image") Environment.DIRECTORY_PICTURES else Environment.DIRECTORY_MOVIES),
            "/slideshow"
        )
        if (!dir.exists()) dir.mkdirs()

        val filePath = Util.queryName(contentResolver, uri)
        val extension = filePath.substring(filePath.lastIndexOf("."))
        val destination = File(dir.absolutePath, "/$filename$extension")
        Log.d("BackgroundFragment", "$destination")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            val out = FileOutputStream(destination)
            val buf = ByteArray(16384)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            inputStream.close()
            out.close()
        }

        output.invoke(destination.toString(), type)
    }

}