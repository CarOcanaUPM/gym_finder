package com.example.gymfindermadrid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.*;

import com.example.gymfindermadrid.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private GimnasiosAdapter adapter;
    private ArrayList<Gimnasio> gimnasios = new ArrayList<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "gimnasios-db").allowMainThreadQueries().build();


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = null;
        if (fragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) fragment).getNavController();
        }

        if (navController != null) {
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        } else {
            Log.e("NavController", "No se pudo encontrar el NavController");
        }

        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GimnasiosAdapter(this, gimnasios);
        binding.recyclerView.setAdapter(adapter);

        obtenerGimnasios();
    }

    private void obtenerGimnasios() {
        if (hayConexionInternet()) {
            // Hay Internet: pedir a la API
            String apiKey = getString(R.string.google_maps_key);
            GimnasiosApiService service = RetrofitClient.getClient().create(GimnasiosApiService.class);
            service.getGimnasios("40.416775,-3.703790", 5000, "gym", apiKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            respuesta -> {
                                if (respuesta != null && respuesta.results != null) {
                                    gimnasios.clear();
                                    gimnasios.addAll(respuesta.results);
                                    adapter.notifyDataSetChanged();

                                    // Guardar en la base de datos si no existían
                                    for (Gimnasio gimnasio : respuesta.results) {
                                        guardarGimnasioSiNoExiste(gimnasio);
                                    }
                                } else {
                                    Log.e("API", "No se recibieron datos de la API");
                                }
                            },
                            error -> Log.e("API Error", "Error al obtener gimnasios: " + error.toString())
                    );
        } else {
            // No hay Internet: cargar desde base de datos
            List<Gimnasio> gimnasiosLocales = db.gimnasioDao().obtenerTodos();

            gimnasios.clear();
            for (Gimnasio entidad : gimnasiosLocales) {
                gimnasios.add(entidad);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void guardarGimnasioSiNoExiste(Gimnasio gimnasio) {
        new Thread(() -> {
            Gimnasio existente = db.gimnasioDao().obtenerGimnasioPorId(gimnasio.getPlaceId());
            if (existente == null) {
                db.gimnasioDao().insertarGimnasio(gimnasio);
                Log.d("DB", "Gimnasio guardado: " + gimnasio.getNombre());
            } else {
                Log.d("DB", "Gimnasio ya existe en la BD: " + gimnasio.getNombre());
            }
        }).start();
    }


    void obtenerDetallesGimnasio(String placeId) {
        String apiKey = getString(R.string.google_maps_key);
        GimnasiosApiService service = RetrofitClient.getClient().create(GimnasiosApiService.class);
        service.getGimnasioDetalles(placeId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        detalle -> {

                            Log.d("Gimnasio Detalle", "Teléfono: " + detalle.getResult().getPhoneNumber());
                            Log.d("Gimnasio Detalle", "Website: " + detalle.getResult().getWebsite());

                        },
                        error -> Log.e("API Error", "Error al obtener detalles del gimnasio: " + error.toString())
                );
    }

    private boolean hayConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = null;
        if (fragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) fragment).getNavController();
        }
        return navController != null && NavigationUI.navigateUp(navController, appBarConfiguration);
    }
}
