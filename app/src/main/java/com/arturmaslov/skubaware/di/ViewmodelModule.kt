package com.arturmaslov.skubaware.di

import com.arturmaslov.skubaware.viewmodel.BaseVM
import com.arturmaslov.skubaware.viewmodel.MainVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BaseVM(get()) }
    viewModel {
        MainVM(
            get(),
            get()
        )
    }
}