package com.display.sholat.ui.settings.background

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.display.sholat.data.entity.SlideShow
import com.display.sholat.databinding.LayoutItemBackgroundBinding
import com.display.sholat.util.initializeFocusZoom
import com.display.sholat.util.toView
import com.yqritc.scalablevideoview.ScalableVideoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackgroundAdapter(private val callbackCurrentFocus: (slideShow: SlideShow) -> Unit) : RecyclerView.Adapter<BackgroundAdapter.ViewHolder>()   {

    val currentList = ArrayList<SlideShow>()
    var selected: Pair<Int, ViewHolder>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemBackgroundBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = currentList[position]
        var view: View? = null
        CoroutineScope(Dispatchers.Main).launch {
            view = item.toView(binding.root.context)
            view?.isFocusable = false
            (binding.content as ViewGroup).let {
                it.addView(view, 0)
                if (view is ScalableVideoView) {
                    (view as ScalableVideoView).pause()
                }
            }
        }

        binding.root.initializeFocusZoom(1.1f) {_, b ->
            if (view != null && view is ScalableVideoView) {
                if (b) (view as ScalableVideoView).start()
                else (view as ScalableVideoView).pause()
            }
        }

        binding.radioButton.isChecked = item.id == selected?.first

        binding.radioButton.isSelected = false
        binding.radioButton.setOnClickListener {
            selected?.let { it.second.binding.radioButton.isChecked = false }
            binding.radioButton.isChecked = true
            selected = Pair(item.id, holder)
        }

        binding.root.setOnClickListener {
            selected?.let { it.second.binding.radioButton.isChecked = false }
            binding.radioButton.isChecked = true
            selected = Pair(item.id, holder)
            callbackCurrentFocus.invoke(item)
        }
    }

    override fun getItemCount() = currentList.size

    fun summitList(list: List<SlideShow>) {
        currentList.clear()
        currentList.addAll(list)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = currentList[position]

    fun find(id: Int) = currentList.find { it.id == id }

    fun add(slideShow: SlideShow) {
        if (itemCount > 0) {
            val nextId = getItem(itemCount-1).id
            currentList.add(slideShow.copy(id = nextId))
            notifyItemInserted(itemCount-1)
        } else {
            currentList.add(slideShow.copy(id = 1))
            notifyItemInserted(0)
        }
    }

    fun delete(slideShow: SlideShow) {
        val position = currentList.indexOf(slideShow)
        currentList.remove(slideShow)
        notifyItemRemoved(position)
    }

    fun update(slideShow: SlideShow) {
        currentList.find { it.id == slideShow.id }?.let {
            val position = currentList.indexOf(it)
            currentList.remove(it)
            currentList.add(position, slideShow)
            notifyItemChanged(position)
        }

    }

    class ViewHolder(val binding: LayoutItemBackgroundBinding) : RecyclerView.ViewHolder(binding.root)
}