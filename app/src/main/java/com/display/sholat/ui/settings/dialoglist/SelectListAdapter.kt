package com.display.sholat.ui.settings.dialoglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.display.sholat.databinding.LayoutItemRadioButtonBinding
import com.display.sholat.util.initializeFocusZoom

/**
 * Created by Jumadi Janjaya date on 30/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/


class SelectListAdapter : RecyclerView.Adapter<SelectListAdapter.ViewHolder>() {

    val currentList = ArrayList<Pair<String, String>>()
    private val allList = ArrayList<Pair<String, String>>()

    var selectedId = ""

    fun search(query: String) {
        val list = if (query.isNotEmpty()) allList.filter { it.second.lowercase().contains(query.lowercase()) } else allList
        currentList.clear()
        currentList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemRadioButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        binding.root.initializeFocusZoom(1.1f)

        binding.tvName.text = item.second
        binding.radioButton.isChecked = item.first == selectedId

        binding.radioButton.tag = item.first
        binding.radioButton.isSelected = false
        binding.radioButton.setOnClickListener {
            if (it.tag is String) {
                selectedId = it.tag as String
                notifyDataSetChanged()
            }
        }
        binding.root.setOnClickListener {
            if (binding.radioButton.tag is String) {
                selectedId = binding.radioButton.tag as String
                notifyDataSetChanged()
            }
        }

    }

    fun getItem(position: Int) = currentList[position]

    fun getPosition(id: String) : Int {
        val find = currentList.findLast { it.first == id }
        return if (find != null) currentList.indexOf(find)
        else 0
    }

    override fun getItemCount() = currentList.size

    fun summitList(list: List<Pair<String, String>>) {
        allList.clear()
        allList.addAll(list)
        currentList.clear()
        currentList.addAll(list)
        notifyDataSetChanged()
    }


    class ViewHolder(val binding: LayoutItemRadioButtonBinding) : RecyclerView.ViewHolder(binding.root)
}