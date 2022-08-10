package com.display.sholat.di

import com.display.sholat.ui.home.HomeFragment
import com.display.sholat.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment() : HomeFragment

    @ContributesAndroidInjector(modules = [SubFragmentModule::class])
    abstract fun contributeSettingsFragment() : SettingsFragment



}