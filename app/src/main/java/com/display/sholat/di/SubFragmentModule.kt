package com.display.sholat.di

import com.display.sholat.ui.settings.about.AboutFragment
import com.display.sholat.ui.settings.background.BackgroundSettingFragment
import com.display.sholat.ui.settings.datetime.DateTimeSettingFragment
import com.display.sholat.ui.settings.info.InfoSettingFragment
import com.display.sholat.ui.settings.iqomah.IqomahSettingFragment
import com.display.sholat.ui.settings.language.LanguageSettingFragment
import com.display.sholat.ui.settings.prayer.DialogSyncFragment
import com.display.sholat.ui.settings.prayer.PrayerSettingFragment
import com.display.sholat.ui.settings.runningtext.RunningTextSettingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SubFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeBackgroundSettingFragment() : BackgroundSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeDateTimeSettingFragment() : DateTimeSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoSettingFragment() : InfoSettingFragment

    @ContributesAndroidInjector
    abstract fun contributePrayerSettingFragment() : PrayerSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeRunningTextSettingFragment() : RunningTextSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeDialogSyncFragment() : DialogSyncFragment

    @ContributesAndroidInjector
    abstract fun contributeLanguageSettingFragment() : LanguageSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeIqomahSettingFragment() : IqomahSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutSettingFragment() : AboutFragment
}