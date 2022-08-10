package com.display.sholat.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.display.sholat.App
import com.display.sholat.R
import com.display.sholat.base.BaseFragment
import com.display.sholat.data.entity.*
import com.display.sholat.databinding.FragmentHomeBinding
import com.display.sholat.databinding.LayoutItemCardBinding
import com.display.sholat.ui.AppActivity
import com.display.sholat.ui.MainActivity
import com.display.sholat.util.DateHijri
import com.display.sholat.util.SlideShowManager
import com.display.sholat.util.Util
import com.display.sholat.util.isValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jumadi Janjaya date on 29/09/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class HomeFragment : BaseFragment<HomeViewModel>(HomeViewModel::class) {

    companion object {
        const val SAVE_STATE = "save_state"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var slideShowManager: SlideShowManager

    private var homeUiModel: HomeUiModel? = null
    private var countDownTimer: CountDownTimer? = null
    private var isPrayer = false

    private val thread = Thread {

        while (!Thread.currentThread().isInterrupted) {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val timeZone = TimeZone.getTimeZone(homeUiModel?.timeZoneId)
                    if (TimeZone.getDefault().id != timeZone.id) App.setTimeZoneId(homeUiModel?.timeZoneId)
                    val date = App.getDate()

                    val hijri =
                        if (homeUiModel?.rangeHijri == "0-0-0") DateHijri(date).writeIslamicDate()
                        else Util.listRangeHijri(homeUiModel?.rangeHijri ?: "0-0-0")
                    val hijriDate = "${hijri.day} ${hijri.monthName}, ${hijri.year} H"

                    binding.tvTime.text = Util.dateFormat("HH:mm:ss", Date().time)
                    binding.tvDate.text = Util.dateFormat("EEEE, dd MMMM yyyy", date.time)
                    binding.tvDateHijri.text = hijriDate
                    //untuk jam fullscreen
                    binding.tvTime2.text = Util.dateFormat("HH:mm:ss", Date().time)
                    binding.tvDate2.text = Util.dateFormat("EEEE, dd MMMM yyyy", date.time)
                    binding.tvDateHijri2.text = hijriDate

                    homeUiModel?.prayer?.let {
                        launch(Dispatchers.IO) {
                            val list = it.toList()
                            val current = list.find { v -> checkPrayerCurrent(v.second) }
                            if (current?.second != homeUiModel?.currentPrayer) {
                                launch(Dispatchers.Main) {
                                    val find =
                                        list.find { v -> v.second == homeUiModel?.currentPrayer }
                                    startIqomah(find?.first)
                                    showPrayer(it)
                                }
                            }
                        }
                    }

                    homeUiModel?.currentRunningText?.let {
                        // if (!Util.isInRangeDate(it.scheduleStart, it.scheduleEnd)) {
                        //     updateRunningText()
                        // }
                    }

                    if (this@HomeFragment::slideShowManager.isInitialized) {
                        slideShowManager.tick()
                    }
                }
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (e: Exception) {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.layoutContent.setOnClickListener {
            if (!isPrayer) switchFullScreen()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            val uiModel = savedInstanceState.getParcelable<HomeUiModel>(SAVE_STATE)
            if (uiModel != null) {
                initInfo(uiModel)
                uiModel.currentRunningText?.let { showRunningText(it) }
                uiModel.prayer?.let { showPrayer(it) }
            }
        }

        viewModel.getUiModel().observe(viewLifecycleOwner) {

            initInfo(it)
            initPlayer()
            initRunningText()
            initSlideShow()

            viewModel.getCurrentPrayer()
            viewModel.getCurrentRunningText()
            viewModel.getCurrentSlideShow()

        }
    }

    private fun initInfo(uiModel: HomeUiModel) = with(uiModel) {
        homeUiModel = this
        binding.tvNameMasjid.text = nameMosque
        binding.tvAddressMasjid.text = addressMosque
    }

    private fun initRunningText() {
        viewModel.responseRunningText.observe(viewLifecycleOwner) { list ->
            binding.tvRunningText.animation?.cancel()

            homeUiModel = if (isPrayer) {
                homeUiModel?.copy(runningTexts = list.filter { it.isShowInIqomah })
            } else {
                setScreenNormal()
                homeUiModel?.copy(runningTexts = list.filter { !it.isShowInIqomah })
            }
            updateRunningText()
        }
    }

    private fun updateRunningText() {
        homeUiModel?.runningTexts?.let { list ->
            val find = list.findLast {
                (it.scheduleStart == 0L && it.scheduleEnd == 0L) || Util.isInRangeDate(
                    it.scheduleStart,
                    it.scheduleEnd
                )
            }
            if (find != null) {
                homeUiModel = homeUiModel?.copy(currentRunningText = find)
                if (isPrayer) showRunningText2(find) else showRunningText(find)
            }
        }
    }

    private fun switchFullScreen() {
        val switch = binding.layoutHeader.isVisible
        if (switch) setFullScreen() else setScreenNormal()
    }

    private fun setFullScreen() {

        if (!binding.layoutHeader.isVisible) return

        if (requireActivity() is MainActivity)
            (requireActivity() as MainActivity).setFullScreen()
        else
            (requireActivity() as AppActivity).setFullScreen()

        binding.layoutHeader.animate().translationY(-binding.layoutHeader.height.toFloat())
            .withEndAction {
                binding.layoutHeader.isVisible = false
            }
        binding.layoutInfo.animate().translationX(binding.layoutInfo.width.toFloat())
            .withEndAction {
                binding.layoutInfo.isVisible = false
            }
        binding.layoutFooter.animate().translationY(binding.layoutFooter.height.toFloat())
            .withEndAction {
                binding.layoutFooter.isVisible = false
                binding.frameSlide.requestLayout()

            }
        binding.layoutContent.isFocusable = true
        binding.layoutContent.requestFocus()
    }

    private fun setScreenNormal() {
        if (binding.layoutHeader.isVisible) return

        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).setScreenNormal()
        } else {
            (requireActivity() as AppActivity).setScreenNormal()
        }
        binding.layoutHeader.isVisible = true
        binding.layoutFooter.isVisible = true
        binding.layoutInfo.isVisible = true
        binding.layoutHeader.animate().translationY(0f)
        binding.layoutInfo.animate().translationX(0f)
        binding.layoutFooter.animate().translationY(0f).withEndAction {
            binding.frameSlide.requestLayout()
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }


    private fun showRunningText(runningText: RunningText) = with(runningText) {
        homeUiModel = homeUiModel?.copy(currentRunningText = this)
        binding.tvRunningText.text = text
        val animation = AlphaAnimation(0.9f, 1f)
        animation.duration = speed
        animation.interpolator = LinearInterpolator()
        binding.tvRunningText.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                nextRunningText()
            }

            override fun onAnimationRepeat(p0: Animation?) {}

        })
    }

    private fun showRunningText2(runningText: RunningText) = with(runningText) {
        homeUiModel = homeUiModel?.copy(currentRunningText = this)
        binding.tvRunningText2.text = text
        val animation = AlphaAnimation(0.9f, 1f)//TranslateAnimation(1500f, -1500f, 0f, 0f)
        animation.duration = speed
        //animation.repeatMode = Animation.RESTART
        //animation.repeatCount = Animation.INFINITE
        animation.interpolator = LinearInterpolator()
        binding.tvRunningText2.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                nextRunningText()
            }

            override fun onAnimationRepeat(p0: Animation?) {}

        })
    }

    private fun nextRunningText() {
        Log.e("MainActivity", "nextRunningText")
        homeUiModel?.runningTexts?.let { list ->
            val find = list.find { it == homeUiModel?.currentRunningText }
            if (find != null) {
                var isNext = false
                var index = list.indexOf(find) + 1
                if (index >= list.size) index = 0
                for ((i, v) in list.withIndex()) {
                    if (i >= index) {
                        if ((v.scheduleStart == 0L && v.scheduleEnd == 0L) || Util.isInRangeDate(
                                v.scheduleStart,
                                v.scheduleEnd
                            )
                        ) {
                            isNext = true
                            if (isPrayer) showRunningText2(v) else showRunningText(v)
                            break
                        }
                    }
                }
                if (!isNext) {
                    if (isPrayer) showRunningText2(find) else showRunningText(find)
                }
            }
        }
    }

    private fun initSlideShow() {
        viewModel.responseSlideShow.observe(viewLifecycleOwner) { listSlide ->
            val newList = listSlide.filter { filterSlideShow(it) }
            Log.e("Slide", newList.toString())
            slideShowManager = SlideShowManager(binding.frameSlide, newList).apply {
                callbackSlide = { slideShow ->
                    CoroutineScope(Dispatchers.Main).launch {
                        if (!slideShow.isShowInIqomah) {
                            if (slideShow.isFullScreen){
                                setFullScreen()
                                binding.tvTime2.setAutoSize()
                                binding.fulClock.isVisible = slideShow.showClock

                            } else {
                                setScreenNormal()
                                //jam full
                                binding.fulClock.isVisible = false
                            }
                        } else {
                            setFullScreen()
                        }
                    }
                    this.list = listSlide.filter { filterSlideShow(it) }
                }
                startSlide()
            }
        }
    }

    private fun filterSlideShow(slideShow: SlideShow): Boolean {
        //val current = homeUiModel?.prayer?.toList(checkSeninKamis())?.find { it.second == homeUiModel?.currentPrayer }?.first
        val isDate = Util.isInRangeDate(slideShow.scheduleStart, slideShow.scheduleEnd)
        val isTime = Util.isInRangeTime(slideShow.scheduleTimeStart, slideShow.scheduleTimeEnd)
        Log.d("HomeFragment", "$isDate $isTime")

        return if (isPrayer) slideShow.isShowInIqomah else isDate && isTime && !slideShow.isShowInIqomah
    }

    private fun initPlayer() =
        viewModel.responsePrayer.observe(viewLifecycleOwner) { showPrayer(it) }

    private fun showPrayer(prayer: Prayer) = with(prayer) {
        val current = this.toList().find { checkPrayerCurrent(it.second) }?.second
        homeUiModel = homeUiModel?.copy(prayer = this, currentPrayer = current)
        Log.d("HomeFragment", "$current")

        binding.cardSubuh.setData(App.string.fajr, subuh, current == subuh)
        binding.cardSyuruq.setData(App.string.syuruq, syuruq, current == syuruq)
        binding.cardZhuhur.setData(App.string.dhuhr, dzuhur, current == dzuhur)
        binding.cardAshar.setData(App.string.asr, ashar, current == ashar)
        binding.cardMagrib.setData(App.string.maghrib, maghrib, current == maghrib)
        binding.cardIsya.setData(App.string.isha, isya, current == isya)
    }

    private fun checkPrayerCurrent(time: String?) = !time.isNullOrEmpty() && time.isValid()

    private fun checkSeninKamis(): Boolean {
        val cal = Calendar.getInstance()
        cal.time = App.getDate()
        val week = cal[Calendar.DAY_OF_WEEK]
        Log.e("Home", "week $week")
        return week == 2 || week == 5
    }

    private fun isJumat(): Boolean {
        val cal = Calendar.getInstance()
        cal.time = App.getDate()
        val week = cal[Calendar.DAY_OF_WEEK]
        Log.e("Home", "week $week")
        return week == 6
    }

    private fun LayoutItemCardBinding.setData(
        nameId: String,
        time: String,
        isCurrent: Boolean = false
    ) {
        tvName.text = nameId
        tvTime.text = time
        if (isCurrent) {
            cardView.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.selected_background,
                    requireActivity().theme
                )
            )
        } else {
            cardView.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    android.R.color.white,
                    requireActivity().theme
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        countDownTimer?.cancel()
        try {
            thread.start()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        //startDownTimer()
        try {
            thread.interrupt()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startIqomah(current: String?) {
        val iqomah = homeUiModel!!.iqomah!!.toList(checkSeninKamis())
        iqomah.findLast { it.first == current }?.let {
            Log.e("Home", it.toString())
            val timeAdzan = Util.timeToLong(homeUiModel!!.currentPrayer!!)
            val timeIqomah = Util.timeToLong(it.second)
            Log.e("Home", "time ${(timeIqomah)}")
            homeUiModel = homeUiModel!!.copy(currentIqomah = timeIqomah)
            if (isJumat() && current.equals(getString(R.string.dhuhr), true))
                startKhutbah()
            else
                startDownTimer(current!!)
        }
    }

    private fun startDownTimer(prayerName: String) {
        if (homeUiModel?.currentIqomah ?: 0L <= 0) return
        val dateNow = Util.getDate("yyyy-MM-dd", Util.dateFormat(time = Date().time))!!.time
        val iqomahTime = dateNow + homeUiModel!!.currentIqomah
        var countTimer = iqomahTime - Date().time
        Log.e("Home", "countTimer $countTimer")
        binding.tvIqomah.isVisible = true
        binding.tvIqomahNow.isVisible = false
        binding.viewRunning.isVisible = true
        binding.fulClock.isVisible = false
        slideShowManager.stopSlide()

        binding.tvIqomah.setAutoSize()
        if (binding.layoutHeader.isVisible)  setFullScreen()

        isPrayer = true
        viewModel.getCurrentRunningText()
        viewModel.getCurrentSlideShow()

        countDownTimer = object : CountDownTimer(countTimer, 1000) {
            override fun onTick(p0: Long) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.tvIqomah.text = dateFormat(countTimer)
                    countTimer -= 1000
                }
            }

            override fun onFinish() {
                homeUiModel = homeUiModel?.copy(currentIqomah = 0L)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.tvIqomah.text = "00:00"
                    delay(1000)
                    binding.tvIqomah.isVisible = false

                    val name = prayerName.substring(0, 1).uppercase() + prayerName.substring(1)

                    binding.tvIqomahNow.isVisible = true
                    binding.tvIqomahNow.text =
                        if (isJumat() && prayerName == getString(R.string.dhuhr)) App.string.now_khutbah else App.string.now_iqomah.formatter(
                            name
                        )

                    delay((2 * 60000 + 30000).toLong())
                    isPrayer = false

                    binding.viewRunning.isVisible = false
                    binding.tvIqomahNow.isVisible = false
                    countDownTimer = null

                    Log.e("Home", "isPrayer $isPrayer")
                    slideShowManager.stopSlide()
                    viewModel.getCurrentRunningText()
                    viewModel.getCurrentSlideShow()
                }
            }
        }.start()
    }

    private fun startKhutbah() {
        val dateNow = Util.getDate("yyyy-MM-dd", Util.dateFormat(time = Date().time))!!.time
        val iqomahTime = dateNow + homeUiModel!!.currentIqomah
        val countTimer = iqomahTime - Date().time
        Log.e("Home", "countTimer $countTimer")
        binding.tvIqomah.isVisible = true
        binding.tvIqomahNow.isVisible = false
        binding.viewRunning.isVisible = false
        binding.fulClock.isVisible = false
        slideShowManager.stopSlide()

        binding.tvIqomah.setAutoSize()
        if (binding.layoutHeader.isVisible) setFullScreen()

        isPrayer = true
        viewModel.getCurrentRunningText()
        viewModel.getCurrentSlideShow()

        CoroutineScope(Dispatchers.Main).launch {
            binding.tvIqomah.isVisible = false
            binding.tvIqomahNow.isVisible = true
            binding.tvIqomahNow.text = App.string.now_khutbah

            // delay((2 * 60000 + 30000).toLong())
            //delay jadi 30m
            delay((30 * 60000).toLong())
            isPrayer = false

            binding.viewRunning.isVisible = false
            binding.tvIqomahNow.isVisible = false
            countDownTimer = null

            Log.e("Home", "isPrayer $isPrayer")
            slideShowManager.stopSlide()
            viewModel.getCurrentRunningText()
            viewModel.getCurrentSlideShow()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormat(time: Long): String {
        val current = SimpleDateFormat("mm:ss")
        return current.format(Date(time))
    }
}