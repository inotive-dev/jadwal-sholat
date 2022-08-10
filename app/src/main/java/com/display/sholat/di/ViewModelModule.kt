package com.display.sholat.di

import androidx.lifecycle.ViewModel
import com.display.sholat.ui.home.HomeViewModel
import com.display.sholat.ui.settings.background.SlideViewModel
import com.display.sholat.ui.settings.datetime.DateTimeViewModel
import com.display.sholat.ui.settings.info.InfoViewModel
import com.display.sholat.ui.settings.iqomah.IqomahViewModel
import com.display.sholat.ui.settings.language.LangViewModel
import com.display.sholat.ui.settings.prayer.PrayerViewModel
import com.display.sholat.ui.settings.runningtext.RunningTextViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun provideHomeViewModel(binds: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SlideViewModel::class)
    abstract fun provideSlideViewModel(binds: SlideViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DateTimeViewModel::class)
    abstract fun provideDateTimeViewModel(binds: DateTimeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InfoViewModel::class)
    abstract fun provideInfoViewModel(binds: InfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrayerViewModel::class)
    abstract fun providePrayerViewModel(binds: PrayerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RunningTextViewModel::class)
    abstract fun provideRunningTextViewModel(binds: RunningTextViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LangViewModel::class)
    abstract fun provideLangViewModel(binds: LangViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IqomahViewModel::class)
    abstract fun provideIqomahViewModel(binds: IqomahViewModel): ViewModel
}