package com.example.gymfindermadrid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

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

    // Authentication
    private boolean isAuthenticated = false;
    private ISingleAccountPublicClientApplication mSingleAccountApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar estado de autenticación
        isAuthenticated = getIntent().getBooleanExtra("IS_AUTHENTICATED", false);
        Log.d("MainActivity", "Usuario autenticado: " + isAuthenticated);

        // Inicializar base de datos
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "gimnasios-db").allowMainThreadQueries().build();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupNavigation();
        setupRecyclerView();
        setupFab();
        initializeMsal();
        obtenerGimnasios();
    }

    private void setupNavigation() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = null;
        if (fragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) fragment).getNavController();
        }

        if (navController != null) {
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GimnasiosAdapter(this, gimnasios);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fab.setOnClickListener(view -> {
            if (isAuthenticated) {
                Snackbar.make(view, "Usuario autenticado - Funciones premium disponibles", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Perfil", v -> showUserProfile()).show();
            } else {
                Snackbar.make(view, "Inicia sesión para acceder a más funciones", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Login", v -> goToLogin()).show();
            }
        });
    }

    private void initializeMsal() {
        if (isAuthenticated) {
            PublicClientApplication.createSingleAccountPublicClientApplication(
                    getApplicationContext(),
                    R.raw.auth_config,
                    new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                        @Override
                        public void onCreated(ISingleAccountPublicClientApplication application) {
                            mSingleAccountApp = application;
                        }

                        @Override
                        public void onError(MsalException exception) {
                            Log.e("MainActivity", "Error inicializando MSAL", exception);
                        }
                    }
            );
        }
    }

    private void obtenerGimnasios() {
        if (hayConexionInternet()) {
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
                                        guardarGimnasioSiNoExiste(gimnasio);
                                    }

                                    Toast.makeText(this, "Gimnasios cargados: " + respuesta.results.size(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> {
                                Log.e("API Error", "Error al obtener gimnasios: " + error.toString());
                                cargarGimnasiosLocales();
                            }
                    );
        } else {
            cargarGimnasiosLocales();
        }
    }

    private void cargarGimnasiosLocales() {
        new Thread(() -> {
            List<Gimnasio> gimnasiosLocales = db.gimnasioDao().obtenerTodos();
            runOnUiThread(() -> {
                gimnasios.clear();
                gimnasios.addAll(gimnasiosLocales);
                adapter.notifyDataSetChanged();

                if (gimnasiosLocales.isEmpty()) {
                    Toast.makeText(this, "No hay gimnasios guardados. Conéctate a internet.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Mostrando gimnasios guardados: " + gimnasiosLocales.size(), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void guardarGimnasioSiNoExiste(Gimnasio gimnasio) {
        new Thread(() -> {
            Gimnasio existente = db.gimnasioDao().obtenerGimnasioPorId(gimnasio.getPlaceId());
            if (existente == null) {
                db.gimnasioDao().insertarGimnasio(gimnasio);
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

    private void showUserProfile() {
        if (mSingleAccountApp != null) {
            mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
                @Override
                public void onAccountLoaded(com.microsoft.identity.client.IAccount activeAccount) {
                    if (activeAccount != null) {
                        Toast.makeText(MainActivity.this, "Usuario: " + activeAccount.getUsername(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onAccountChanged(com.microsoft.identity.client.IAccount priorAccount, com.microsoft.identity.client.IAccount currentAccount) {
                    // Handle account change
                }

                @Override
                public void onError(MsalException exception) {
                    Log.e("MainActivity", "Error obteniendo perfil", exception);
                }
            });
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout && isAuthenticated) {
            performLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private void performLogout() {
        if (mSingleAccountApp != null) {
            mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
                @Override
                public void onSignOut() {
                    Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }

                @Override
                public void onError(MsalException exception) {
                    Log.e("MainActivity", "Error al cerrar sesión", exception);
                    Toast.makeText(MainActivity.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            });
        }
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