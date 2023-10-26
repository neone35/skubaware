package com.arturmaslov.tgnba.di

import android.app.Application
import androidx.room.Room
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.data.source.local.LocalDataSource
import com.arturmaslov.skubaware.data.source.local.LocalDatabase
import com.arturmaslov.skubaware.data.source.remote.Api
import com.arturmaslov.skubaware.data.source.remote.RemoteDataSource
import com.arturmaslov.tgnba.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repoModule = module {
    single { provideMainRepository(get(), get(), get()) }
    single { provideLocalDataSource(get(), get()) }
    single { provideRemoteDataSource(get(), get()) }
    single { provideDispatcherIO() }
    single { provideLocalDatabase(androidApplication()) }
}

fun provideLocalDatabase(app: Application): LocalDatabase {
    val dbName = Constants.DATABASE_NAME
    return Room.databaseBuilder(
        app,
        LocalDatabase::class.java, dbName
    )
        .fallbackToDestructiveMigration()
        .build()
}

private fun provideDispatcherIO() = Dispatchers.IO

private fun provideMainRepository(
    dispatcher: CoroutineDispatcher,
    api: Api,
    localDB: LocalDatabase
): MainRepository {
    val localDataSource = provideLocalDataSource(localDB, dispatcher)
    val remoteDataSource = provideRemoteDataSource(api, dispatcher)
    return MainRepository(localDataSource, remoteDataSource)
}

private fun provideLocalDataSource(
    localDB: LocalDatabase,
    dispatcher: CoroutineDispatcher
): LocalDataSource {
    return LocalDataSource(localDB, dispatcher)
}

private fun provideRemoteDataSource(
    api: Api,
    dispatcher: CoroutineDispatcher
): RemoteDataSource {
    return RemoteDataSource(api, dispatcher)
}