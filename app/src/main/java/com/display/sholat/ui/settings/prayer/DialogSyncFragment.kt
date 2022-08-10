package com.display.sholat.ui.settings.prayer

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.display.sholat.App
import com.display.sholat.R
import com.display.sholat.data.entity.Prayer
import com.display.sholat.data.repository.Result
import com.display.sholat.databinding.FragmentDialogPrayerBinding
import com.display.sholat.databinding.LayoutItemMenuEditBinding
import com.display.sholat.di.ViewModelFactory
import com.display.sholat.util.initializeFocusZoom
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DialogSyncFragment(private val city: String, private var prayer: Prayer, private val callback: (prayer: Prayer) -> Unit) : DialogFragment(),
    HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy { ViewModelProvider(this, viewModelFactory).get(PrayerViewModel::class.java) }
    private lateinit var binding: FragmentDialogPrayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogPrayerBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvTitle.text = App.string.title_sync_data
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
            callback.invoke(prayer)
            dismiss()
        }

        initPlayer(prayer)

        viewModel.updatePlayer(city).observe(viewLifecycleOwner) {
            when(it) {
                Result.Loading -> {}
                is Result.Success -> initPlayer(it.data.data.prayer)
                is Result.Error -> Toast.makeText(requireContext(), it.t.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun initPlayer(prayer: Prayer) {
        Log.e("iniPrayer", "$prayer")
        this@DialogSyncFragment.prayer = prayer
        binding.imsyak.create(R.string.syuruq, prayer.syuruq)
        binding.shubu.create(R.string.fajr, prayer.subuh)
        binding.zhuhur.create(R.string.dhuhr, prayer.dzuhur)
        binding.ashar.create(R.string.asr, prayer.ashar)
        binding.magrib.create(R.string.maghrib, prayer.maghrib)
        binding.isha.create(R.string.isha, prayer.isya)
    }

    private fun LayoutItemMenuEditBinding.create(@StringRes nameId: Int, value: String) {
        root.isFocusable = false
        root.setBackgroundResource(R.drawable.background_menu_dialog)
        tvName.setText(nameId)
        tvValue.text = value
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}