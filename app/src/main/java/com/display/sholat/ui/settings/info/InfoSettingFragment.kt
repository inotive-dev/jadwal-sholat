package com.display.sholat.ui.settings.info

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.display.sholat.App
import com.display.sholat.base.BaseFragment
import com.display.sholat.databinding.FragmentInfoSettingBinding
import com.display.sholat.util.readTextFromUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by Jumadi Janjaya date on 30/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/


open class InfoSettingFragment : BaseFragment<InfoViewModel>(InfoViewModel::class) {

    companion object {
        const val READ_REQUEST_CODE = 200
        const val REQUEST_PERMISSION_STORAGE = 300
    }
    private lateinit var binding: FragmentInfoSettingBinding
    private lateinit var infoUiModel: InfoUiModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoSettingBinding.inflate(inflater)
        binding.tvTitle.text = App.string.title_edit_information_mosque
        binding.btnUpdate.text = App.string.update
//        binding.btnImport.text = App.string.import_text
        binding.textName.text = App.string.name_mosque
        binding.textAddress.text = App.string.address
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUpdate.setOnClickListener {
            viewModel.save(infoUiModel)
            Toast.makeText(requireContext(), App.string.toast_successfully_updated, Toast.LENGTH_LONG).show()
        }

//        binding.btnImport.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
//            } else {
//                performFileSearch()
//            }
//        }

        binding.editName.doOnTextChanged { _, _, _, _ ->
            infoUiModel = infoUiModel.copy(nameMosque = binding.editName.text.toString())
        }

        binding.editAddress.doOnTextChanged { _, _, _, _ ->
            infoUiModel = infoUiModel.copy(addressMosque = binding.editAddress.text.toString())
        }

        viewModel.getUiModel().observe(viewLifecycleOwner) {
            initData(it)
        }
        CoroutineScope(Dispatchers.Main).launch {
            binding.editName.let {
                it.isFocusable = true
                //it.requestFocus()
            }
        }
    }

    private fun performFileSearch() {
        val intent = Intent()
        intent.type = "text/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select text"), READ_REQUEST_CODE)
    }


    private fun initData(uiModel: InfoUiModel) = with(uiModel) {
        infoUiModel = this
        binding.editName.setText(nameMosque)
        binding.editAddress.setText(addressMosque)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data
                if (uri != null) {
                    val text = requireActivity().readTextFromUri(uri)
                    val texts = text.split("#")
                    if (texts.size == 2) {
                        initData(infoUiModel.copy(nameMosque =  texts[0], addressMosque = texts[1]))
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) performFileSearch()
            else Toast.makeText(requireContext(), "Permission not granted!", Toast.LENGTH_LONG).show()
        }
    }

}