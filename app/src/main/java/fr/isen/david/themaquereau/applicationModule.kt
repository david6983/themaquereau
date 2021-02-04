package fr.isen.david.themaquereau

import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val applicationModule = module {
    single { AppPreferencesHelperImpl(androidContext()) }
    single { ApiHelperImpl(androidContext()) }
}