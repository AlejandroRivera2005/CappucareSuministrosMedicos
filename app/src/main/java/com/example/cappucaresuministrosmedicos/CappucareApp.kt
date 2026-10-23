package com.example.cappucaresuministrosmedicos

import android.app.Application
import com.example.cappucaresuministrosmedicos.model.SembradorDatos
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CappucareApp : Application() {

    @Inject
    lateinit var sembrador: SembradorDatos

    override fun onCreate() {
        super.onCreate()   // aquí Hilt inyecta 'sembrador'
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            sembrador.sembrarProductosDemo()
        }
    }
}