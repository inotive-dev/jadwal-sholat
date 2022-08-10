package com.display.sholat.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.display.sholat.di.ViewModelFactory
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseActivity<VM: ViewModel>(klassViewModel: KClass<VM>) : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected val viewModel: VM by lazy { ViewModelProvider(this, viewModelFactory).get(klassViewModel.java) }

}