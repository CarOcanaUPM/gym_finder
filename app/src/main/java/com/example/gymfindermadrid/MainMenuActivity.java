package com.example.gymfindermadrid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btnVerGimnasios = findViewById(R.id.btnVerGimnasios);
        Button btnEscanearCodigo = findViewById(R.id.btnEscanearCodigo);

        btnVerGimnasios.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnEscanearCodigo.setOnClickListener(v -> {
            startActivity(new Intent(this, EscanearCodigoActivity.class));
        });
    }
}
