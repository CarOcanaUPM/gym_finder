package com.example.gymfindermadrid;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gymfindermadrid.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private GimnasiosAdapter adapter;
    private ArrayList<Gimnasio> gimnasios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


                                for (Gimnasio gimnasio : respuesta.results) {
                                    Log.d("API_RESPONSE", "Nombre: " + gimnasio.getNombre());
                                    Log.d("API_RESPONSE", "Dirección: " + gimnasio.getDireccion());
                                    Log.d("API_RESPONSE", "Puntuación: " + gimnasio.getPuntuacion());
                                    Log.d("API_RESPONSE", "Place ID: " + gimnasio.getPlaceId());
                                }
                            } else {
                                Log.e("API", "No se recibieron datos de la API");
                            }
                        },
                        error -> Log.e("API Error", "Error al obtener gimnasios: " + error.toString())
                );
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
