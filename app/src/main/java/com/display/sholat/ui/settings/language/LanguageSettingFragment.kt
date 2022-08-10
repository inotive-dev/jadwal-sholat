package com.display.sholat.ui.settings.language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.display.sholat.App
import com.display.sholat.base.BaseFragment
import com.display.sholat.data.entity.Language
import com.display.sholat.data.repository.Result
import com.display.sholat.databinding.FragmentLanguageSettingBinding
import com.display.sholat.ui.MainActivity
import com.display.sholat.ui.settings.dialoglist.SelectListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class LanguageSettingFragment : BaseFragment<LangViewModel>(LangViewModel::class) {

    private lateinit var binding: FragmentLanguageSettingBinding
    private val selectListAdapter: SelectListAdapter by lazy { SelectListAdapter() }
//    private var listLang = ArrayList<Lang>()
    private var listLang = ArrayList<Language>()

    companion object {
        private var list: ArrayList<Pair<String, String>> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locales: Array<Locale> = Locale.getAvailableLocales()
        for (i in locales.indices) {
            val locale = locales[i]
            //if (list.findLast { it.first ==  locale.language } == null) {
                //list.add(Pair(locale.language, locale.displayName))
            //}
            //else continue
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageSettingBinding.inflate(inflater)
        binding.tvTitle.text = App.string.title_select_language
        binding.btnSet.text = App.string.set
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectListAdapter
        }

        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            selectListAdapter.search(text.toString())
        }
        binding.editSearch.isVisible = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("xlogx","getCountryList A")

        viewModel.getLanguageList().observe(viewLifecycleOwner) {
            when(it) {
                is Result.Error -> {
                    Log.e("xlogx","getLanguage AC "+ it.t.printStackTrace())
                    //Toast.makeText(requireContext(), App.string.toast_error_download_language, Toast.LENGTH_LONG).show()
                }
                Result.Loading -> {}
                is Result.Success -> {
                    listLang = ArrayList(it.data)
                    val l = it.data.map { lang -> Pair(lang.code, lang.country) }
                    list.addAll(l)
                    selectListAdapter.summitList(l)

                    Log.e("xlogx","getLanguage BA")
                }
            }
        }

        viewModel.getLanguage().observe(viewLifecycleOwner) {
            selectListAdapter.selectedId = it
            CoroutineScope(Dispatchers.Main).launch {
//                delay(200)
//                delay(200)
                binding.rvList.smoothScrollToPosition(selectListAdapter.getPosition(it))
            }
        }

        viewModel.responseLangStrings.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Error -> {
                    Toast.makeText(requireContext(), App.string.toast_error_download_language, Toast.LENGTH_LONG).show()
                }
                Result.Loading -> {}
                is Result.Success -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.clear()
                        viewModel.saveLangDisplayName(listLang.find { lang -> lang.code == selectListAdapter.selectedId }!!.country)
                        viewModel.saveStrings(it.data)
                        delay(200)
                        val dm = resources.displayMetrics
                        val conf = resources.configuration
                        conf.setLocale(Locale(it.data.translate_to.lowercase()))
                        resources.updateConfiguration(conf, dm)
                        val refresh = Intent(requireContext(), MainActivity::class.java)
                        refresh.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        requireActivity().finish()
                        startActivity(refresh)

                        Log.e("xlogx","get-what A")
                    }
                }
            }
        }

        binding.btnSet.setOnClickListener {
            viewModel.getLangStrings(selectListAdapter.selectedId)
        }
    }
}