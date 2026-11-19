package com.example.cappucaresuministrosmedicos.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import com.example.cappucaresuministrosmedicos.model.remote.InventarioApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

@HiltViewModel
class ConexionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: InventarioApi
) : ViewModel() {
    val internetDisponible: Flow<Boolean> = callbackFlow {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        fun conectado(): Boolean {
            val red = manager.activeNetwork ?: return false
            val capacidades = manager.getNetworkCapabilities(red) ?: return false
            return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(conectado()) }
            override fun onLost(network: Network) { trySend(conectado()) }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(conectado())
            }
        }
        manager.registerDefaultNetworkCallback(callback)
        trySend(conectado())
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    suspend fun apiDisponible(): Boolean = withContext(Dispatchers.IO) {
        withTimeoutOrNull(7000L) {
            try {
                api.listarProductos()
                true
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                false
            }
        } ?: false
    }
}