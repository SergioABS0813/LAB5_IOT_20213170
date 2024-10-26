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
        String tipo = intent.getStringExtra("tipo");

        String titulo = "Recordatorio de Consumo de Alimento"; // Título predeterminado
        if ("motivacion".equals(tipo)) {
            titulo = "Notificación de Motivación";  // Cambia el título si es motivación
        } else if ("recordatorio_no_comida".equals(tipo)) {
            titulo = "Recordatorio de Comida";
            mensaje = "No se ingresó ninguna comida durante el día, mañana vas con todo. ¡Tú puedes, vamos!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "calorias_channel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void mostrarNotificacion(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "calorias_channel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Recordatorio de Comida")
                .setContentText("No se ingresó ninguna comida durante el día, mañana vas con todo. ¡Tú puedes, vamos!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2, builder.build());
    }



}
