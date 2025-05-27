package com.example.gymfindermadrid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private TextInputEditText emailEdit, passwordEdit;
    private Button loginButton, continueButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeMsal();
        setupClickListeners();
    }

    private void initializeViews() {
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        continueButton = findViewById(R.id.continueButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initializeMsal() {
        PublicClientApplication.createSingleAccountPublicClientApplication(
                getApplicationContext(),
                R.raw.auth_config,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                        Log.d(TAG, "MSAL inicializado correctamente");
                        checkForExistingAccount();
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e(TAG, "Error inicializando MSAL", exception);
                        Toast.makeText(LoginActivity.this, "Error de configuración", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void checkForExistingAccount() {
        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(com.microsoft.identity.client.IAccount activeAccount) {
                if (activeAccount != null) {
                    Log.d(TAG, "Usuario ya autenticado: " + activeAccount.getUsername());
                    navigateToMainActivity(true);
                }
            }

            @Override
            public void onAccountChanged(com.microsoft.identity.client.IAccount priorAccount, com.microsoft.identity.client.IAccount currentAccount) {
                if (currentAccount == null) {
                    Log.d(TAG, "Usuario desconectado");
                }
            }

            @Override
            public void onError(MsalException exception) {
                Log.e(TAG, "Error verificando cuenta existente", exception);
            }
        });
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> performLogin());
        continueButton.setOnClickListener(v -> navigateToMainActivity(false));
    }

    private void performLogin() {
        showProgress(true);

        String[] scopes = {"User.Read"};

        mSingleAccountApp.signIn(this, null, scopes, getAuthInteractiveCallback());
    }

    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d(TAG, "Login exitoso para: " + authenticationResult.getAccount().getUsername());
                showProgress(false);
                Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity(true);
            }

            @Override
            public void onError(MsalException exception) {
                Log.e(TAG, "Error en login", exception);
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login cancelado por el usuario");
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Login cancelado", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void navigateToMainActivity(boolean isAuthenticated) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("IS_AUTHENTICATED", isAuthenticated);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);
        continueButton.setEnabled(!show);
        emailEdit.setEnabled(!show);
        passwordEdit.setEnabled(!show);
    }
}