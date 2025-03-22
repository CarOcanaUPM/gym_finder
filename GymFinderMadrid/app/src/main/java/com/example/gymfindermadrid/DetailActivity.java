package com.example.gymfindermadrid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    private TextView textNombre, textDireccion, textRating, textTelefono, textWebsite;
    private ImageView imageGimnasio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        textNombre = findViewById(R.id.textNombre);
        textDireccion = findViewById(R.id.textDireccion);
        textRating = findViewById(R.id.textRating);
        textTelefono = findViewById(R.id.textTelefono);
        textWebsite = findViewById(R.id.textWebsite);
        imageGimnasio = findViewById(R.id.gymImage);


        Intent intent = getIntent();
        if (intent != null) {
            String nombre = intent.getStringExtra("NOMBRE_GIMNASIO");
            String direccion = intent.getStringExtra("DIRECCION_GIMNASIO");
            String rating = intent.getStringExtra("RATING_GIMNASIO");
            String telefono = intent.getStringExtra("TELEFONO_GIMNASIO");
            String website = intent.getStringExtra("WEBSITE_GIMNASIO");
            String imageUrl = intent.getStringExtra("IMAGE_URL");


            textNombre.setText(nombre != null ? nombre : "No disponible");
            textDireccion.setText(direccion != null ? direccion : "No disponible");
            textRating.setText(rating != null ? "Puntuación: " + rating : "Puntuación: No disponible");
            textTelefono.setText(telefono != null ? "Teléfono: " + telefono : "Teléfono: No disponible");
            textWebsite.setText(website != null ? "Website: " + website : "Website: No disponible");


            if (imageUrl != null) {
                Glide.with(this).load(imageUrl).into(imageGimnasio);
            } else {
                imageGimnasio.setImageResource(R.drawable.gym_background);
            }
        } else {
            Log.e("DetailActivity", "No se pasaron datos en el Intent");
        }
    }
}
