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

        // Se recepciona lo enviado en ConsumoActivity
        String tipo = intent.getStringExtra("tipo");

        if ("recordatorio_no_comida".equals(tipo)) {
            ConsumoActivity consumoActivity = new ConsumoActivity();
            if (consumoActivity.comidaList.isEmpty()) {
                mostrarNotificacion(context);
            }
        }
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
