package com.arturmaslov.skubaware.di

import com.arturmaslov.skubaware.data.usecase.GetLocalProductsUseCase
import com.arturmaslov.skubaware.data.usecase.UpdateLocalWithRemoteUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory {
        UpdateLocalWithRemoteUseCase(get())
    }
    factory {
        GetLocalProductsUseCase(get())
    }
}

