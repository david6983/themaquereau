package fr.isen.david.themaquereau

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TheMaquereauApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // start koin with the module list
        startKoin {
            androidContext(this@TheMaquereauApplication)
            androidLogger()
            modules(applicationModule)
        }
    }
}