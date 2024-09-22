package com.example.lab3_20201638;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTimer extends AppCompatActivity {


    private CountDownTimer contador_tiempo;
    private TextView textViewName, textViewEmail, textViewTimer, desc;
    private ImageView genero_icon;
    private ImageButton play_but;
    private String nombre, apellido, correo, genero;
    private Integer id;
    private boolean temporizador_on_off = false;
    private long tiempoTotal = 1500000;
    private long tiempoDescanso =  300000;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitApi.getApiService();

        textViewName = findViewById(R.id.nombre_user);
        textViewEmail = findViewById(R.id.correo);
        textViewTimer = findViewById(R.id.tiempoTxt);
        play_but = findViewById(R.id.play_but);
        genero_icon = findViewById(R.id.genero_icon);
        desc = findViewById(R.id.desc);

        nombre = getIntent().getStringExtra("nombre");
        apellido = getIntent().getStringExtra("apellido");
        correo = getIntent().getStringExtra("correo");
        genero = getIntent().getStringExtra("genero");
        id = getIntent().getIntExtra("id", -1);
        textViewName.setText(nombre + ' ' + apellido);
        textViewEmail.setText(correo);


        if (genero != null) {
            if (genero.equalsIgnoreCase("male")) {
                genero_icon.setImageResource(R.drawable.male);
            } else if (genero.equalsIgnoreCase("female")) {
                genero_icon.setImageResource(R.drawable.female);
            }
        }

        tiempo_now();

        play_but.setOnClickListener(v -> {
            if (temporizador_on_off) {
                reiniciarContador();
            } else {
                if (tiempoTotal == 1500000) {
                    iniciarTimer();
                } else {
                    tiempoTotal = 1500000;
                    tiempo_now();
                    desc.setText("Descanso: 05:00");
                    play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_play_arrow));
                    temporizador_on_off = false;
                }
            }
        });

        ImageButton logoutButton = findViewById(R.id.logout_butt);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });

    }
    private void mostrarDialogoConfirmacion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cerrar_app();
                    }
                })
                .show();
    }

    private void reiniciarContador() {
        if (contador_tiempo != null) {
            contador_tiempo.cancel();
        }
        tiempoTotal = 1500000;
        tiempo_now();
        play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_play_arrow));
        temporizador_on_off = false;
    }

    private void iniciarTimer() {
        contador_tiempo = new CountDownTimer(tiempoTotal, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                tiempoTotal = millisUntilFinished;
                tiempo_now();
            }



            @Override
            public void onFinish() {
                temporizador_on_off = false;
                play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_play_arrow));

                apiService.getUserTasks(id).enqueue(new Callback<Tarea_Resp>() {
                    @Override
                    public void onResponse(Call<Tarea_Resp> call, Response<Tarea_Resp> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Tareas> tareas = response.body().getTodos();

                            if (!tareas.isEmpty()) {
                                Intent intent = new Intent(ActivityTimer.this, Tarea_activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                intent.putParcelableArrayListExtra("tasks", new ArrayList<>(tareas));
                                startActivity(intent);
                                desc.setText("En descanso");
                                empezarTiempoDescanso();
                                play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_restart_alt));
                                play_but.setVisibility(View.GONE);
                            } else {
                                desc.setText("En descanso");
                                new MaterialAlertDialogBuilder(ActivityTimer.this)
                                        .setTitle("¡Felicidades!")
                                        .setMessage("Empezó el tiempo de descanso!")
                                        .setPositiveButton("Entendido", (dialog, which) -> {
                                            empezarTiempoDescanso();
                                        })
                                        .setCancelable(false)
                                        .show();

                                play_but.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("TimerActivity", "Error al obtener las tareas");
                        }
                    }

                    @Override
                    public void onFailure(Call<Tarea_Resp> call, Throwable t) {
                        Log.e("TimerActivity", "Error al obtener las tareas: " + t.getMessage());
                    }
                });
            }
        }.start();

        temporizador_on_off = true;
        play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_restart_alt));
    }

    private void empezarTiempoDescanso() {
        contador_tiempo = new CountDownTimer(tiempoDescanso, 1000) {

            @Override
            public void onTick(long tiempo_antes_de_terminar) {
                tiempoTotal = tiempo_antes_de_terminar;
                tiempo_now();
            }

            @Override
            public void onFinish() {
                temporizador_on_off = false;
                play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_restart_alt));

                new MaterialAlertDialogBuilder(ActivityTimer.this)
                        .setTitle("Atención")
                        .setMessage("Terminó el tiempo de descanso. Dale al botón de reinicio para iniciar otro ciclo.")
                        .setPositiveButton("Entendido", (dialog, which) -> {
                            play_but.setVisibility(View.VISIBLE);
                            play_but.setImageDrawable(getResources().getDrawable(R.drawable.round_restart_alt));
                        })
                        .setCancelable(false)
                        .show();
            }
        }.start();
    }

    private void tiempo_now() {
        int minutes = (int) (tiempoTotal / 1000) / 60;
        int seconds = (int) (tiempoTotal / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeFormatted);
    }

    private void cerrar_app() {
        Intent intent = new Intent(ActivityTimer.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}