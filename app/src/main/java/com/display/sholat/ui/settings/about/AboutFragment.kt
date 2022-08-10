package com.display.sholat.ui.settings.about

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.display.sholat.App
import com.display.sholat.R
import com.display.sholat.base.BaseFragment
import com.display.sholat.databinding.FragmentAboutBinding

class AboutFragment : BaseFragment<AboutViewModel>(AboutViewModel::class) {
    private lateinit var binding : FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater)
        binding.tvTitle.text = App.string.title_about
        binding.tvWeb.paintFlags = binding.tvWeb.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            tvWeb.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.web_content)))
                startActivity(intent)
            }
        }
    }

}