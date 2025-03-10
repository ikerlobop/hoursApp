// MainActivity.java
package com.example.hoursapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText entradaLunes, entradaLunesMin;
    private EditText entradaMartes, entradaMartesMin;
    private EditText entradaMiercoles, entradaMiercolesMin;
    private EditText entradaJueves, entradaJuevesMin;
    private EditText entradaViernes, entradaViernesMin;
    private EditText entradaSabado, entradaSabadoMin;
    private EditText entradaDomingo, entradaDomingoMin;
    private DatePicker datePicker;
    private Button botonCalcular;
    private TextView resultadoTotal, resultadoPromedio, resultadoExtras, resultadoMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar DatePicker
        datePicker = findViewById(R.id.date_picker);

        // Inicializar campos de horas y minutos
        entradaLunes = findViewById(R.id.entrada_lunes);
        entradaLunesMin = findViewById(R.id.entrada_lunes_min);
        entradaMartes = findViewById(R.id.entrada_martes);
        entradaMartesMin = findViewById(R.id.entrada_martes_min);
        entradaMiercoles = findViewById(R.id.entrada_miercoles);
        entradaMiercolesMin = findViewById(R.id.entrada_miercoles_min);
        entradaJueves = findViewById(R.id.entrada_jueves);
        entradaJuevesMin = findViewById(R.id.entrada_jueves_min);
        entradaViernes = findViewById(R.id.entrada_viernes);
        entradaViernesMin = findViewById(R.id.entrada_viernes_min);
        entradaSabado = findViewById(R.id.entrada_sabado);
        entradaSabadoMin = findViewById(R.id.entrada_sabado_min);
        entradaDomingo = findViewById(R.id.entrada_domingo);
        entradaDomingoMin = findViewById(R.id.entrada_domingo_min);

        // Botón y resultados
        botonCalcular = findViewById(R.id.boton_calcular);
        resultadoTotal = findViewById(R.id.resultado_total);
        resultadoPromedio = findViewById(R.id.resultado_promedio);
        resultadoExtras = findViewById(R.id.resultado_extras);
        resultadoMes = findViewById(R.id.resultado_mes);

        // Configurar listener para el botón
        botonCalcular.setOnClickListener(v -> calcularHoras());
    }

    private void calcularHoras() {
        try {
            // Obtener año y mes seleccionados
            int año = datePicker.getYear();
            int mes = datePicker.getMonth();
            String nombreMes = new SimpleDateFormat("MMMM", Locale.getDefault()).format(
                    new Calendar.Builder().setDate(año, mes, 1).build().getTime());

            // Obtener y convertir inputs como horas y minutos
            List<Double> horas = Arrays.asList(
                    convertirAHoras(entradaLunes, entradaLunesMin),
                    convertirAHoras(entradaMartes, entradaMartesMin),
                    convertirAHoras(entradaMiercoles, entradaMiercolesMin),
                    convertirAHoras(entradaJueves, entradaJuevesMin),
                    convertirAHoras(entradaViernes, entradaViernesMin),
                    convertirAHoras(entradaSabado, entradaSabadoMin),
                    convertirAHoras(entradaDomingo, entradaDomingoMin)
            );

            // Calcular estadísticas
            double total = 0;
            for (Double hora : horas) {
                total += hora;
            }

            double promedio = total / 7;
            double extras = (total > 40) ? total - 40 : 0.0;

            // Calcular días laborables en el mes
            Calendar calendario = Calendar.getInstance();
            calendario.set(año, mes, 1);
            int diasEnMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
            int diasLaborables = calcularDiasLaborables(año, mes);

            // Estimar total mensual (proyección basada en promedio de días laborables)
            double totalMensual = (total / 7) * diasLaborables;

            // Formatear resultados
            DecimalFormat formato = new DecimalFormat("#.##");

            // Mostrar resultados
            resultadoTotal.setText("Total semana: " + formato.format(total) + " horas");
            resultadoPromedio.setText("Promedio diario: " + formato.format(promedio) + " horas");
            resultadoExtras.setText("Horas extras (>40h): " + formato.format(extras) + " horas");
            resultadoMes.setText("Proyección " + nombreMes + " (" + diasLaborables + " días laborables): "
                    + formato.format(totalMensual) + " horas");

        } catch (Exception e) {
            Toast.makeText(this, "Error en los datos ingresados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int calcularDiasLaborables(int año, int mes) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(año, mes, 1);
        int diasEnMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);

        int diasLaborables = 0;
        for (int dia = 1; dia <= diasEnMes; dia++) {
            calendario.set(año, mes, dia);
            int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);
            // Contar de lunes a viernes (Calendar.MONDAY es 2, Calendar.FRIDAY es 6)
            if (diaSemana >= Calendar.MONDAY && diaSemana <= Calendar.FRIDAY) {
                diasLaborables++;
            }
        }
        return diasLaborables;
    }

    private double convertirAHoras(EditText campoHoras, EditText campoMinutos) {
        int horas = getIntValue(campoHoras);
        int minutos = getIntValue(campoMinutos);

        return horas + (minutos / 60.0);
    }

    private int getIntValue(EditText editText) {
        String texto = editText.getText().toString().trim();
        return texto.isEmpty() ? 0 : Integer.parseInt(texto);
    }
}