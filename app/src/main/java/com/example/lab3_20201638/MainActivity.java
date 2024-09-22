package com.example.lab3_20201638;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText user, password;
    private Button Login_butt;

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

        user = findViewById(R.id.user_Name);
        password = findViewById(R.id.contra);
        Login_butt = findViewById(R.id.log_butt);

        Login_butt.setOnClickListener(v -> login());

    }


    private void login() {
        String username = user.getText().toString().trim();
        String contra = password.getText().toString().trim();

        if (username.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Debe ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Login_pedido log = new Login_pedido(username, contra);
        RetrofitApi.getApiService().login(log).enqueue(new Callback<Login_Resp>() {
            @Override
            public void onResponse(Call<Login_Resp> call, Response<Login_Resp> response) {
                if (response.isSuccessful()) {
                    Login_Resp logresp = response.body();
                    Toast.makeText(MainActivity.this, " Bienvenido " +
                            logresp.getFirstName(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ActivityTimer.class);
                    intent.putExtra("nombre", logresp.getFirstName());
                    intent.putExtra("correo", logresp.getEmail());
                    intent.putExtra("apellido", logresp.getLastName());
                    intent.putExtra("genero", logresp.getGender());
                    Integer userId = logresp.getId();
                    intent.putExtra("id", userId);
                    startActivity(intent);


                } else {
                    Toast.makeText(MainActivity.this, "El usuario o la contraseña sn incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login_Resp> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Conexión fallida", Toast.LENGTH_SHORT).show();
            }
        });
    }
}