package com.example.lab5_iot_20213170;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btn_hacia_objetivo;
    EditText input_peso, input_altura, input_edad;
    RadioGroup radioGroup_genero;
    Spinner spinner_nivel_actividad, spinner_objetivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        input_peso = findViewById(R.id.input_peso);
        input_altura = findViewById(R.id.input_altura);
        input_edad = findViewById(R.id.input_edad);
        btn_hacia_objetivo = findViewById(R.id.btn_hacia_objetivo);
        radioGroup_genero = findViewById(R.id.radioGroup_genero);
        spinner_nivel_actividad = findViewById(R.id.spinner_nivel_actividad);
        spinner_objetivo = findViewById(R.id.spinner_objetivo);

        btn_hacia_objetivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String peso = input_peso.getText().toString();
                String altura = input_altura.getText().toString();
                String edad = input_edad.getText().toString();

                // Validamos si los campos de EditText están vacíos
                if (peso.isEmpty() || altura.isEmpty() || edad.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor complete todos los campos de Peso, Altura y Edad.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validamos si el usuario coloca 0 a uno de los edittext (No se especificó alguna restricción más sobre estos parámetros)
                if (peso.equals("0")) {
                    Toast.makeText(MainActivity.this, "El peso no puede ser cero", Toast.LENGTH_SHORT).show();
                    return;
                } else if (altura.equals("0")) {
                    Toast.makeText(MainActivity.this, "La altura no puede ser cero", Toast.LENGTH_SHORT).show();
                    return;
                } else if (edad.equals("0")) {
                    Toast.makeText(MainActivity.this, "La edad no puede ser cero", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Validamos si se ha seleccionado un género
                int selectedGeneroId = radioGroup_genero.getCheckedRadioButtonId();
                if (selectedGeneroId == -1) {  // Si no hay ningún RadioButton seleccionado
                    Toast.makeText(MainActivity.this, "Por favor seleccione un género.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtenemos el RadioButton de género seleccionado
                RadioButton selectedGenero = findViewById(selectedGeneroId);
                String genero = selectedGenero.getText().toString();

                // Obtenemos el nivel de actividad seleccionado en el Spinner
                String nivelActividad = spinner_nivel_actividad.getSelectedItem().toString();

                // Obtenemos el objetivo seleccionado en el Spinner
                String objetivo = spinner_objetivo.getSelectedItem().toString();

                // Luego de las validaciones, se hace el calculo del TMB + nivel de actividad:

                double pesoKg = Double.parseDouble(peso);
                double alturaCm = Double.parseDouble(altura);
                int edadAnos = Integer.parseInt(edad);
                double tmb;
                if (genero.equals("Masculino")) {
                    tmb = (10 * pesoKg) + (6.25 * alturaCm) - (5 * edadAnos) + 5;
                } else { //Para Femenino
                    tmb = (10 * pesoKg) + (6.25 * alturaCm) - (5 * edadAnos) - 161;
                }

                double caloriasTotales = tmb; //inicialmente
                if (nivelActividad.equals("Sedentario")){
                    caloriasTotales *= 1.2;
                } else if (nivelActividad.equals("Ligera actividad")) {
                    caloriasTotales *= 1.375;
                } else if (nivelActividad.equals("Actividad moderada")) {
                    caloriasTotales *= 1.55;
                } else if (nivelActividad.equals("Alta actividad")) {
                    caloriasTotales *= 1.725;
                }else{
                    caloriasTotales *= 1.9;
                }

                // Ajuste según el objetivo del usuario
                if (objetivo.equals("Bajar de peso")) {
                    caloriasTotales -= 300;
                } else if (objetivo.equals("Subir de peso")) {
                    caloriasTotales += 500;
                }

                // Mostrar el resultado al usuario
                Toast.makeText(MainActivity.this,
                        "Calorías necesarias por día: " + caloriasTotales,
                        Toast.LENGTH_LONG).show();

                Toast.makeText(MainActivity.this,
                        "Peso: " + peso + " kg, Altura: " + altura + " cm, Edad: " + edad +
                                "\nGénero: " + genero +
                                "\nNivel de Actividad: " + nivelActividad +
                                "\nObjetivo: " + objetivo,
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, ConsumoActivity.class);
                intent.putExtra("caloriasTotales", caloriasTotales);
                startActivity(intent);


            }
        });

    }


}