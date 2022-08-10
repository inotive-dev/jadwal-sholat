package com.display.sholat.ui

import android.os.Bundle
import android.view.ViewPropertyAnimator
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.display.sholat.R
import com.display.sholat.databinding.ActivityMainBinding
import com.display.sholat.databinding.LayoutItemMenuWithIconBinding
import com.display.sholat.ui.home.HomeFragment
import com.display.sholat.ui.settings.SettingsFragment
import com.display.sholat.util.dpToPx

/**
 * Created by Jumadi Janjaya date on 28/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/


class AppActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private var toggle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //window.decorView.apply {
        //    systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        //}
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.btnHome.create(R.drawable.ic_baseline_home_24) {
                if (!binding.btnHome.select.isVisible) gotoHome()
            }
            binding.btnSetting.create(R.drawable.ic_baseline_settings_24) {
                if (!binding.btnSetting.select.isVisible) gotoSetting()
            }
            binding.btnHome.root.let {
                it.isFocusable = true
                it.requestFocus()
            }
        }

        binding.btnArrow.setOnClickListener { switchToggle() }
    }

    private fun switchToggle() {
        if (toggle) {
            toggle = false
            hideMenu().withEndAction {
                binding.menuNavigation.isVisible = false
                arrowMenuForward()
            }
        } else {
            toggle = true
            binding.menuNavigation.isVisible = true
            showMenu().withEndAction { arrowMenuBack() }
        }
    }

    private fun arrowMenuBack() {
        binding.btnArrow.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24)
        val lp = binding.btnArrow.layoutParams
        lp.width = 40f.dpToPx()
        binding.btnArrow.layoutParams = lp
    }

    private fun arrowMenuForward() {
        binding.btnArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        val lp = binding.btnArrow.layoutParams
        lp.width = 20f.dpToPx()
        binding.btnArrow.layoutParams = lp
    }

    private fun gotoHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_fragment, HomeFragment())
            .commitAllowingStateLoss()

        binding.btnSetting.let {
            it.root.alpha = 0.5f
            it.select.isVisible = false
        }
    }

    private fun gotoSetting() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_fragment, SettingsFragment())
            .commit()

        binding.btnHome.let {
            it.root.alpha = 0.5f
            it.select.isVisible = false
        }
    }

    private fun LayoutItemMenuWithIconBinding.create(@DrawableRes iconId: Int, callbackSelect: () -> Unit) {
        imgIcon.setImageResource(iconId)
        root.setOnFocusChangeListener { _, b ->
            if (b) {
                callbackSelect.invoke()
                select.isVisible = true
                root.alpha = 1f
            }
        }
        root.setOnClickListener { callbackSelect.invoke() }
    }

    fun setFullScreen() {
        binding.btnArrow.animate().translationX(-binding.btnArrow.width.toFloat())
        hideMenu().withEndAction {
            toggle = false
            binding.menuNavigation.isVisible = false
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun setScreenNormal() {
        binding.btnArrow.animate().translationX(0f)
        showMenu().withEndAction {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            toggle = true
            arrowMenuBack()
        }
    }

    private fun hideMenu(): ViewPropertyAnimator {
        return binding.menuNavigation.animate().translationX(-binding.menuNavigation.width.toFloat())

    }

    private fun showMenu(): ViewPropertyAnimator {
        binding.menuNavigation.isVisible = true
        return binding.menuNavigation.animate().translationX(0f)
    }
}