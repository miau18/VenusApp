package com.example.vnus;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.vnus.ui.auth.home.TelaInicial;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.Continue;

public class MyApp extends Application {

    private static final String ONESIGNAL_APP_ID = "d23bd444-9556-43a1-bb37-9178eb340f08";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
            }
        }));

        OneSignal.getNotifications().addClickListener(event -> {
            SharedPreferences sp = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
            String tipoUsuario = sp.getString("tipo_usuario", "");

            Intent intent;

            if ("psicologo".equals(tipoUsuario)) {
                intent = new Intent(this, TelaDeRelatosPsicologo.class);
            } else if ("usuaria".equals(tipoUsuario)) {
                intent = new Intent(this, TelaDeRelatos.class);
            } else {
                intent = new Intent(this, TelaInicial.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        });
    }
}