package com.example.vnus.ui.auth.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.ui.auth.usuaria.CadastroUsuaria;
import com.example.vnus.ui.auth.usuaria.LoginUsuaria;

public class SegundaTelaUsuaria extends AppCompatActivity {

    private Button btnLogin;
    private Button btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segunda_tela);

        btnLogin = findViewById(R.id.buttonLogin);
        btnCadastro = findViewById(R.id.buttonCadastro);

        btnLogin.setOnClickListener(v ->{
            Intent i = new Intent(SegundaTelaUsuaria.this, LoginUsuaria.class);
            startActivity(i);
        });

        btnCadastro.setOnClickListener(v ->{
            Intent i = new Intent(SegundaTelaUsuaria.this, CadastroUsuaria.class);
            startActivity(i);
        });

    }
}
