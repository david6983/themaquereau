package fr.isen.david.themaquereau

import fr.isen.david.themaquereau.helpers.AppPreferencesHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val applicationModule = module {
    single { AppPreferencesHelper(androidContext()) }
}