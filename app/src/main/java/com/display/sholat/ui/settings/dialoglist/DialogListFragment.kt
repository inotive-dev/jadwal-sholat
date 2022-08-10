package com.display.sholat.ui.settings.dialoglist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.display.sholat.App
import com.display.sholat.databinding.FragmentDialogListBinding
import com.display.sholat.util.initializeFocusZoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Jumadi Janjaya date on 30/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class DialogListFragment(
    private val showSearch: Boolean = false,
    private val title: String,
    private val list: List<Pair<String, String>>,
    private val current: String, private val
    callbackSaved: (id: String) -> Unit) : DialogFragment() {

    lateinit var binding: FragmentDialogListBinding
    private val selectListAdapter: SelectListAdapter by lazy { SelectListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogListBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.title.text = title
        binding.btnCancel.text = App.string.cancel
        binding.btnOk.text = App.string.set

        binding.editSearch.isVisible = showSearch

        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            selectListAdapter.search(text.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.initializeFocusZoom()

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectListAdapter
        }
        selectListAdapter.selectedId = current
        selectListAdapter.summitList(list)
        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            binding.rvList.smoothScrollToPosition(selectListAdapter.getPosition(current))
        }

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnOk.setOnClickListener {
            val selectedId = selectListAdapter.selectedId
            if (selectedId.isNotEmpty()) callbackSaved.invoke(selectedId)
            dismiss()
        }
    }
}