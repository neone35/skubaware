package com.arturmaslov.tgnba.di

import com.arturmaslov.skubaware.viewmodel.BaseVM
import com.arturmaslov.skubaware.viewmodel.MainVM
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainVM(get(), androidApplication()) }
    viewModel { BaseVM(get(), androidApplication()) }
}