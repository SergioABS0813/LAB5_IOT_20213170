package com.example.lab5_iot_20213170;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ConsumoActivity extends AppCompatActivity {

    //Variables globales
    ArrayList<String> comidaList = new ArrayList<>();
    ArrayList<String> caloriasList = new ArrayList<>();
    TextView consumoTextView, calorias_por_consumir_textview, calorias_consumidas_textview;
    double caloriasConsumidas = 0;
    double caloriasTotales;

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

        // Obtener las calorías totales pasadas desde MainActivity
        caloriasTotales = getIntent().getDoubleExtra("caloriasTotales", 0.0);

        // Mostrar las calorías en un TextView o realizar alguna acción
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

        });


    }

    private void mostrarDialogoAgregarComida() {
        // Inflar el diseño del cuadro de diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_agregar_comida, null);

        // Crear el cuadro de diálogo
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        // Campos de entrada en el cuadro de diálogo
        Spinner spinnerComidas = dialogView.findViewById(R.id.spinner_comidas);
        EditText inputComida = dialogView.findViewById(R.id.input_comida);
        EditText inputCalorias = dialogView.findViewById(R.id.input_calorias);

        // Definir las opciones del spinner (nombres de comidas predefinidas)
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
                    inputComida.setText("");  // Limpiar el campo si selecciona "Ninguna"
                    inputCalorias.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });

        // Configurar el cuadro de diálogo
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


    private void actualizarVistaConsumo() {
        if (comidaList.isEmpty()) {
            consumoTextView.setText("No se ha registrado consumo alguno aún");
            calorias_por_consumir_textview.setText("Calorías por consumir hoy: " + String.valueOf(caloriasTotales));
            calorias_consumidas_textview.setText("Has consumido: 0 calorías");
        } else {
            StringBuilder consumoInfo = new StringBuilder();
            for (int i = 0; i < comidaList.size(); i++) {
                consumoInfo.append(String.valueOf(i+1)).append(") Comida: ").append(comidaList.get(i)).append(", Calorías: ").append(caloriasList.get(i)).append("\n").append("\n");
                // Se actualiza el marcador de calorias restantes
                caloriasConsumidas += Double.parseDouble(caloriasList.get(i));
            }
            consumoTextView.setText(consumoInfo.toString());
            calorias_consumidas_textview.setText("Has consumido: " + String.valueOf(caloriasConsumidas) + " calorías");
            if(caloriasTotales-caloriasConsumidas < 0){
                calorias_por_consumir_textview.setText("¡Superaste la meta de calorías!");
            }else{
                calorias_por_consumir_textview.setText("Calorías por consumir hoy: " + String.valueOf(caloriasTotales-caloriasConsumidas));
            }

        }
    }
}