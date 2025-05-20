package com.example.gymfindermadrid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class EscanearCodigoActivity extends AppCompatActivity {

    private ArrayList<String> registros;
    private ArrayAdapter<String> adapter;
    private ListView listaRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_codigo);

        Button btnEscanear = findViewById(R.id.btnEscanearNFC);
        listaRegistros = findViewById(R.id.listaRegistros);

        // Registros simulados
        registros = new ArrayList<>(Arrays.asList(
                "AltaFit Madrid Río | 2024-05-01 09:12 | ID: A1B2C3",
                "BasicFit Getafe | 2024-04-30 17:34 | ID: 11FF22",
                "VivaGym Goya | 2024-04-29 19:45 | ID: FF9900"
        ));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros);
        listaRegistros.setAdapter(adapter);

        btnEscanear.setOnClickListener(v -> {
            startActivity(new Intent(this, EscaneoRealActivity.class));
        });
    }
}
