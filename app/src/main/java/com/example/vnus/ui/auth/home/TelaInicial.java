package com.example.vnus.ui.auth.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;

public class TelaInicial extends AppCompatActivity {

    private Button btnUsuaria, btnPsicologo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);

        btnUsuaria = findViewById(R.id.buttonUsuaria);
        btnPsicologo = findViewById(R.id.buttonPsicologo);

        btnUsuaria.setOnClickListener(v ->{
            Intent i = new Intent(TelaInicial.this, SegundaTelaUsuaria.class);
            startActivity(i);
            finish();
        });

        btnPsicologo.setOnClickListener(v ->{
            Intent i = new Intent(TelaInicial.this, SegundaTelaPsicologo.class);
            startActivity(i);
            finish();
        });
    }
}
