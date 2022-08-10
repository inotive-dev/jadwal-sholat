package com.display.sholat.ui.settings

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.display.sholat.App
import com.display.sholat.R
import com.display.sholat.databinding.FragmentSettingsBinding
import com.display.sholat.databinding.LayoutItemMenuBinding
import com.display.sholat.ui.settings.about.AboutFragment
import com.display.sholat.ui.settings.background.BackgroundSettingFragment
import com.display.sholat.ui.settings.datetime.DateTimeSettingFragment
import com.display.sholat.ui.settings.info.InfoSettingFragment
import com.display.sholat.ui.settings.iqomah.IqomahSettingFragment
import com.display.sholat.ui.settings.language.LanguageSettingFragment
import com.display.sholat.ui.settings.prayer.PrayerSettingFragment
import com.display.sholat.ui.settings.runningtext.RunningTextSettingFragment
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jumadi Janjaya date on 29/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class SettingsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSettingsBinding

    private var current = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        binding.textSettings.text = App.string.title_settings
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext()).load(R.drawable.cami_gca0e589a1_1920).into(binding.imgBackground)
        initMenu()
    }

    private fun initMenu() {

        binding.menuLanguage.setData(App.string.menu_language) { setChildFragment(
            LanguageSettingFragment()
        ) }
        binding.menuDatetime.setData(App.string.menu_date_and_time) { setChildFragment(
            DateTimeSettingFragment()
        ) }
        binding.menuBackground.setData(App.string.menu_background) { setChildFragment(
            BackgroundSettingFragment()
        ) }
        binding.menuRunningText.setData(App.string.menu_running_text) { setChildFragment(
            RunningTextSettingFragment()
        ) }
        binding.menuJadwalShalat.setData(App.string.menu_schedule_prayer) { setChildFragment(
            PrayerSettingFragment()
        ) }
        binding.menuInfo.setData(App.string.menu_info) { setChildFragment(InfoSettingFragment()) }

        binding.menuIqomah.setData(App.string.title_iqamah_setting) { setChildFragment(IqomahSettingFragment()) }

        binding.menuAbout.setData(App.string.title_about) { setChildFragment(AboutFragment()) }

        CoroutineScope(Dispatchers.Main).launch {
                binding.menuLanguage.root.let {
                it.isFocusable = true
                it.requestFocus()
            }
        }
    }

    private fun setChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.content_fragment, fragment)
            .commit()
    }

    private fun LayoutItemMenuBinding.setData(nameId: String, callBackSelect: () -> Unit) {
        tvName.text = nameId
        root.setOnClickListener {
            callBackSelect.invoke()
        }
        root.setOnFocusChangeListener { _, b ->
            select(b)
            if (b && current != nameId) {
                current = nameId
                callBackSelect.invoke()
            }
        }
    }

    private fun LayoutItemMenuBinding.select(isSelect: Boolean) {
        if (isSelect) {
            tvName.setTextColor(Color.WHITE)
            tvName.setTypeface(tvName.typeface, Typeface.BOLD)
            root.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.select_background,
                    requireActivity().theme
                )
            )
        } else {
            tvName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            tvName.setTextColor(ResourcesCompat.getColor(resources, R.color.text_color_select, requireActivity().theme))
            root.setBackgroundColor(Color.TRANSPARENT)
        }
        select.isVisible = isSelect
    }

}