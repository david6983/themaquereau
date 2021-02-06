package fr.isen.david.themaquereau

import fr.isen.david.themaquereau.helpers.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val applicationModule = module {
    single { AppPreferencesHelperImpl(SharedPrefEncryptHelperImpl(androidContext())) }
    single { ApiHelperImpl(androidContext()) }
    single { PersistOrdersHelperImpl(androidContext(), AesEncryptHelperImpl()) }
    single { SharedPrefEncryptHelperImpl(androidContext()) }
    single { AesEncryptHelperImpl() }
}