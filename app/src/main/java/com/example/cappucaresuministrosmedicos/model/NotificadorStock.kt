package com.example.cappucaresuministrosmedicos.model

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificadorStock @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CANAL_ID = "stock_bajo"
    }

    init {
        crearCanal()
    }


    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Alertas de stock",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos cuando un producto queda bajo su nivel mínimo"
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(canal)
        }
    }

    @SuppressLint("MissingPermission") // el permiso se verifica más abajo
    fun avisarStockBajo(producto: ProductoEntity) {
        // Desde Android 13 hay que tener el permiso concedido; si no, no se avisa
        val permitido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        if (!permitido) return


        val abrirApp = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.let {
                PendingIntent.getActivity(
                    context, 0, it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val agotado = producto.stockDisponible <= 0
        val titulo = if (agotado) "Producto agotado" else "Stock bajo el mínimo"
        val texto = "${producto.nombre}: quedan ${producto.stockDisponible} " +
                "${producto.unidadMedida} (mínimo ${producto.stockMinimo})"

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()


        NotificationManagerCompat.from(context).notify(producto.id.toInt(), notificacion)
    }
}