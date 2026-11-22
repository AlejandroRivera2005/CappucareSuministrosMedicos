package com.example.cappucaresuministrosmedicos

import android.app.KeyguardManager
import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cappucaresuministrosmedicos.view.navigation.AppNavHost
import com.example.cappucaresuministrosmedicos.view.theme.CappucareSuministrosMedicosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var desbloqueada by mutableStateOf(false)
    private var autenticando by mutableStateOf(false)
    private var mensaje by mutableStateOf<String?>(null)

    private var solicitud: CancellationSignal? = null
    private var visible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        enableEdgeToEdge()

        setContent {
            CappucareSuministrosMedicosTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()

                    if (!desbloqueada) {
                        BackHandler(enabled = true) {}

                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Text("Cappucare está bloqueada")

                                    Text(
                                        mensaje
                                            ?: "Confirma tu identidad para continuar"
                                    )

                                    if (autenticando) {
                                        CircularProgressIndicator()
                                    } else {
                                        Button(
                                            onClick = {
                                                solicitarAutenticacion()
                                            }
                                        ) {
                                            Text("Desbloquear")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        visible = true

        if (!desbloqueada) {
            solicitarAutenticacion()
        }
    }

    override fun onStop() {
        visible = false
        desbloqueada = false

        val cancelacion = solicitud
        solicitud = null
        autenticando = false

        cancelacion?.cancel()

        super.onStop()
    }

    private fun solicitarAutenticacion() {
        if (
            autenticando ||
            desbloqueada ||
            !visible ||
            isFinishing ||
            isDestroyed
        ) {
            return
        }

        val keyguard = getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager

        if (!keyguard.isDeviceSecure) {
            mensaje =
                "Configura un PIN, patrón o contraseña en los ajustes del dispositivo."
            return
        }

        val autenticadores =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val biometricManager = getSystemService(
            Context.BIOMETRIC_SERVICE
        ) as BiometricManager

        val disponibles = biometricManager.canAuthenticate(
            autenticadores
        )

        if (disponibles != BiometricManager.BIOMETRIC_SUCCESS) {
            mensaje =
                "No se puede iniciar la autenticación. Comprueba la seguridad del dispositivo."
            return
        }

        mensaje = null
        autenticando = true

        val cancelacion = CancellationSignal()
        solicitud = cancelacion

        val prompt = BiometricPrompt.Builder(this)
            .setTitle("Desbloquear Cappucare")
            .setSubtitle(
                "Utiliza tus datos biométricos o el bloqueo del teléfono"
            )
            .setAllowedAuthenticators(autenticadores)
            .build()

        try {
            prompt.authenticate(
                cancelacion,
                mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        if (solicitud !== cancelacion) return

                        solicitud = null
                        autenticando = false
                        mensaje = null

                        desbloqueada = visible
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        if (solicitud !== cancelacion) return

                        solicitud = null
                        autenticando = false
                        mensaje = errString.toString()
                    }

                    override fun onAuthenticationFailed() {
                        if (solicitud !== cancelacion) return

                        mensaje =
                            "No se ha reconocido la identidad. Inténtalo de nuevo."
                    }
                }
            )
        } catch (e: Exception) {
            solicitud = null
            autenticando = false
            mensaje =
                "No se pudo iniciar la autenticación del dispositivo."
        }
    }
}
