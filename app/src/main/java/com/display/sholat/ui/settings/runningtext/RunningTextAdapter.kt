package com.display.sholat.ui.settings.runningtext

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.display.sholat.R
import com.display.sholat.databinding.LayoutItemRunningTextBinding
import com.display.sholat.data.entity.RunningText
import com.display.sholat.util.hideInput
import com.display.sholat.util.initializeFocusZoom

class RunningTextAdapter(
    private val callbackTest: (runningText: RunningText) -> Unit,
    private val callbackDelete: (runningText: RunningText) -> Unit,
    private val callbackEdit: (runningText: RunningText) -> Unit,
    private val callbackSpeed: (runningText: RunningText) -> Unit) : RecyclerView.Adapter<RunningTextAdapter.ViewHolder>() {

    val currentList = ArrayList<RunningText>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemRunningTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val item = currentList[position]
        binding.tvName.text = item.text
        binding.imgEdit.initializeFocusZoom()
        binding.imgDelete.initializeFocusZoom()
        binding.imgSpeed.initializeFocusZoom()

        binding.root.setOnClickListener {
            binding.tvName.callOnClick()
        }

        binding.tvName.setOnClickListener {
            it.requestFocus()
            callbackTest.invoke(item)
        }
        binding.tvName.setOnFocusChangeListener { view, b ->
            if (b) {
                callbackTest.invoke(item)
                binding.root.let {
                    it.setBackgroundResource(R.drawable.bg_round_alpha_border)
                    it.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
                }
            } else binding.root.let {
                it.setBackgroundResource(R.drawable.bg_round_alpha_border)
                it.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            }
        }

        binding.imgDelete.setOnClickListener {
            callbackDelete.invoke(item)
        }

        binding.imgSpeed.setOnClickListener {
            binding.editText.isVisible = false
            binding.tvName.isVisible = true
            val edit = binding.editText.text.toString()
            if (edit != item.text && edit.isNotEmpty()) {
                val i = item.copy(text = binding.editText.text.toString())
                callbackEdit.invoke(i)
                callbackSpeed.invoke(i)
            } else {
                callbackSpeed.invoke(item)
            }
        }

        binding.imgEdit.setOnClickListener {
            binding.tvName.isVisible = false
            binding.editText.let { editText ->
                editText.setText(binding.tvName.text)
                editText.isVisible = true
                editText.requestFocus()
            }
        }

        binding.editText.setOnFocusChangeListener { view, b ->
            if (!b) {
                binding.editText.hideInput(view.windowToken)
                binding.editText.isVisible = false
                binding.tvName.isVisible = true
                if (binding.editText.text.toString() != item.text) {
                    callbackEdit.invoke(item.copy(text = binding.editText.text.toString()))
                }
            }
        }

        binding.editText.doOnTextChanged { text, _, _, _ ->
            binding.tvName.text = text
            callbackEdit.invoke(item.copy(text = text.toString()))
        }

        binding.editText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.editText.hideInput(view.windowToken)
                binding.tvName.text = binding.editText.text.toString()
                binding.editText.isVisible = false
                binding.tvName.isVisible = true
                callbackEdit.invoke(item.copy(text = binding.editText.text.toString()))
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount() = currentList.size

    fun getItem(position: Int) = currentList[position]

    fun find(id: Int) = currentList.find { it.id == id }

    fun summitList(list: List<RunningText>) {
        currentList.clear()
        currentList.addAll(list)
        notifyDataSetChanged()
    }

    fun add(runningText: RunningText) : Int {
        return if (itemCount > 0) {
            val nextId = getItem(itemCount-1).id + 1
            currentList.add(runningText.copy(id = nextId))
            notifyItemInserted(itemCount-1)
            nextId
        } else {
            currentList.add(runningText.copy(id = 0))
            notifyItemInserted(itemCount)
            0
        }
    }

    fun delete(runningText: RunningText) {
        val position = currentList.indexOf(runningText)
        currentList.remove(runningText)
        notifyItemRemoved(position)
    }

    fun update(runningText: RunningText) {
        currentList.find { it.id == runningText.id }?.let {
            val position = currentList.indexOf(it)
            currentList.remove(it)
            currentList.add(position, runningText)
            notifyItemChanged(position)
        }

    }

    class ViewHolder(val binding: LayoutItemRunningTextBinding) : RecyclerView.ViewHolder(binding.root)
}