package com.example.lab5_iot_20213170;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificacionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String mensaje = intent.getStringExtra("mensaje");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "calorias_channel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24) // Cambia a tu icono de notificación
                .setContentTitle("Recordatorio de Consumo de Alimento")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
