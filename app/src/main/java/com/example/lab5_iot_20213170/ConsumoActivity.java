package com.example.lab5_iot_20213170;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class ConsumoActivity extends AppCompatActivity {

    //Variables globales
    ArrayList<String> comidaList = new ArrayList<>();
    ArrayList<String> caloriasList = new ArrayList<>();
    TextView consumoTextView, calorias_por_consumir_textview, calorias_consumidas_textview;
    double caloriasConsumidas = 0;
    double caloriasTotales;
    double caloriasPorConsumir = caloriasTotales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consumo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        caloriasTotales = getIntent().getDoubleExtra("caloriasTotales", 0.0);

        TextView caloriasTextView = findViewById(R.id.calorias_textview);
        caloriasTextView.setText("Calorías necesarias por día: " + caloriasTotales);

        consumoTextView = findViewById(R.id.consumo_textview);
        calorias_por_consumir_textview = findViewById(R.id.calorias_por_consumir_textview);
        calorias_consumidas_textview = findViewById(R.id.calorias_consumidas_textview);
        actualizarVistaConsumo();

        // Botones FAB
        FloatingActionButton fabAgregarComida = findViewById(R.id.fab_agregar_comida);
        FloatingActionButton fab_ejercicios = findViewById(R.id.fab_ejercicios);

        // FAB Comida
        fabAgregarComida.setOnClickListener(v -> {
            // Mostrar el diálogo para añadir comida
            mostrarDialogoAgregarComida();
        });

        // FAB Ejercicios

        fab_ejercicios.setOnClickListener(v ->{
            mostrarDialogoAgregarEjercicio();
        });

        //Notificaciones

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "calorias_channel",
                    "Notificaciones de Calorías",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notificación cuando se exceden las calorías diarias");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Notificaciones en base a la hora
        //Cabe destacar que solo funciona cuando en la hora del celular se tiene 14:00 y 19:00, con ese formato, el cual corresponde a horario 2 y 7 pm respectivamente.
        programarNotificacion(8, 0, "Ingrese el consumo de alimento en el desayuno");
        programarNotificacion(14, 0, "Ingrese el consumo de alimento en el almuerzo");
        programarNotificacion(19, 0, "Ingrese el consumo de alimento en la cena");

        // Notificación al final del día si no ingresó comida alguna
        noIngresoComidaNotificacion();


    }

    private void noIngresoComidaNotificacion() {
        if (comidaList.isEmpty()) {  // Verificamos si no se ha ingresado ningún alimento
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, NotificacionReceiver.class);
            intent.putExtra("tipo", "recordatorio_no_comida"); // Identificar el tipo de notificación
            intent.putExtra("mensaje", "No se ingresó ninguna comida durante el día, mañana vas con todo. ¡Tú puedes, vamos!");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Contando con el formato 11:59 pm
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);

            // Para que se repita diariamente
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }




    private void programarNotificacion(int hora, int minuto, String mensaje) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificacionReceiver.class);
        intent.putExtra("mensaje", mensaje);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, hora * 100 + minuto, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        // Si la hora ya ha pasado, programa para el siguiente día
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void mostrarNotificacionExcesoCalorias() {
        double exceso = Math.abs(caloriasTotales - caloriasConsumidas);
        String mensajeCompleto = "¡Has superado tu meta de calorías diarias! El exceso fue: " + exceso + " calorías. Como sugerencia, podrías hacer más ejercicio o reducir las calorías en la próxima comida. ¡Tú puedes!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "calorias_channel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Límite de Calorías Excedido")
                .setContentText("¡Has superado tu meta de calorías diarias!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mensajeCompleto))  // Usar BigTextStyle para texto más largo: Se usó CHATGPT para la consulta de cómo hacer la notificación un poco más grande para el texto largo que contiene
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void mostrarDialogoAgregarComida() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_agregar_comida, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        Spinner spinnerComidas = dialogView.findViewById(R.id.spinner_comidas);
        EditText inputComida = dialogView.findViewById(R.id.input_comida);
        EditText inputCalorias = dialogView.findViewById(R.id.input_calorias);

        // Definir las opciones del spinner de comidas
        String[] comidasPredefinidas = {"Ninguna", "Pollo", "Huevo", "Pescado", "Yogurt", "Leche"};
        String[] caloriasPredefinidas = {"0", "155", "78", "216", "59", "105"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, comidasPredefinidas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComidas.setAdapter(adapter);

        // Evento para que al seleccionar una comida del Spinner, se llene automáticamente el EditText
        spinnerComidas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Actualizar los EditText con la comida y las calorías seleccionadas
                if (!comidasPredefinidas[position].equals("Ninguna")) {
                    inputComida.setText(comidasPredefinidas[position]);
                    inputCalorias.setText(caloriasPredefinidas[position]);
                } else {
                    inputComida.setText("");
                    inputCalorias.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });

        dialogBuilder
                .setTitle("Añadir Comida")
                .setPositiveButton("Añadir", (dialog, which) -> {
                    // Obtener lo que el usuario ingresó o seleccionó en los campos
                    String comida = inputComida.getText().toString().trim();
                    String calorias = inputCalorias.getText().toString().trim();

                    // Validar que los campos no estén vacíos
                    if (comida.isEmpty() || calorias.isEmpty()) {
                        Toast.makeText(ConsumoActivity.this, "Por favor ingrese ambos campos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Añadir la comida y las calorías a las listas
                        comidaList.add(comida);
                        caloriasList.add(calorias);
                        actualizarVistaConsumo();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss(); // Cerrar el cuadro de diálogo si se cancela
                });

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void mostrarDialogoAgregarEjercicio() {
        // Inflar el diseño del cuadro de diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_agregar_ejercicio, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        EditText inputEjercicio = dialogView.findViewById(R.id.input_ejercicio);
        EditText inputCaloriasEjercicio = dialogView.findViewById(R.id.input_calorias_ejercicio);

        dialogBuilder
                .setTitle("Añadir Ejercicio")
                .setPositiveButton("Añadir", (dialog, which) -> {

                    String ejercicio = inputEjercicio.getText().toString().trim();
                    String caloriasEjercicio = inputCaloriasEjercicio.getText().toString().trim();

                    if (ejercicio.isEmpty() || caloriasEjercicio.isEmpty()) {
                        Toast.makeText(ConsumoActivity.this, "Por favor ingrese ambos campos", Toast.LENGTH_SHORT).show();
                    } else {
                        caloriasConsumidas -= Double.parseDouble(caloriasEjercicio);
                        calorias_consumidas_textview.setText("Has consumido: " + String.valueOf(caloriasConsumidas) + " calorías");
                        caloriasPorConsumir += Double.parseDouble(caloriasEjercicio);
                        calorias_por_consumir_textview.setText("Calorías por consumir hoy: " + String.valueOf(caloriasPorConsumir));

                        /*Se está teniendo en cuenta el hecho de que cuando realice ejercicios BOTE calorías, por lo que las calorías que puede consumir
                        aumenta ya que se RESTA la cantidad de calorías INGRESANTES (por consumo de alimentos) hasta el momento del día menos la cantidad
                        que se botó haciendo ejercicios.*/

                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss(); // Cerrar el cuadro de diálogo si se cancela
                });

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    private void actualizarVistaConsumo() {
        if (comidaList.isEmpty()) {
            consumoTextView.setText("No se ha registrado consumo alguno aún");
            calorias_por_consumir_textview.setText("Calorías por consumir hoy: " + String.valueOf(caloriasTotales));
            calorias_consumidas_textview.setText("Has consumido: 0 calorías");
        } else {
            StringBuilder consumoInfo = new StringBuilder();
            for (int i = 0; i < comidaList.size(); i++) {
                consumoInfo.append(String.valueOf(i+1)).append(") Comida: ").append(comidaList.get(i)).append(", Calorías: ").append(caloriasList.get(i)).append("\n").append("\n");
                // Se actualiza el marcador de calorias restantes (solo para el ultimo que se coloca debido a que ese es el recién agregado)
                if (i == comidaList.size() - 1){
                    caloriasConsumidas += Double.parseDouble(caloriasList.get(i));
                }
            }
            consumoTextView.setText(consumoInfo.toString());
            calorias_consumidas_textview.setText("Has consumido: " + String.valueOf(caloriasConsumidas) + " calorías");
            if(caloriasTotales-caloriasConsumidas < 0){
                calorias_por_consumir_textview.setText("¡Superaste la meta de calorías!");
                mostrarNotificacionExcesoCalorias();

            }else{
                caloriasPorConsumir = caloriasTotales - caloriasConsumidas;
                calorias_por_consumir_textview.setText("Calorías por consumir hoy: " + String.valueOf(caloriasPorConsumir));
            }

            /* En este caso, se tiene en cuenta la suma total de calorías INGRESANTES debido al consumo de algún alimento, esta cantidad es lo que
             se le restará a la cantidad de calorías totales que puede comer en el día */

        }
    }
}