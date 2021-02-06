package fr.isen.david.themaquereau

import fr.isen.david.themaquereau.helpers.ApiHelperImpl
import fr.isen.david.themaquereau.helpers.AppPreferencesHelperImpl
import fr.isen.david.themaquereau.helpers.EncryptHelperImpl
import fr.isen.david.themaquereau.helpers.PersistOrdersHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val applicationModule = module {
    single { AppPreferencesHelperImpl(EncryptHelperImpl(androidContext())) }
    single { ApiHelperImpl(androidContext()) }
    single { PersistOrdersHelperImpl(androidContext(), EncryptHelperImpl(androidContext())) }
    single { EncryptHelperImpl(androidContext()) }
}