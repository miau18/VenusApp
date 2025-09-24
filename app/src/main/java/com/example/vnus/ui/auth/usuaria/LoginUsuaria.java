package com.example.vnus.ui.auth.usuaria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUsuaria extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, senha;
    private EditText codinome, senhaAnonima;
    private Button btnLogin;
    private CheckBox checkAnonimo;
    private LinearLayout layoutNormal, layoutAnonimo;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_usuaria);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Normal
        email = findViewById(R.id.editTextEmail3);
        senha = findViewById(R.id.editTextSenha3);

        // Anônimo
        codinome = findViewById(R.id.editTextCodinomeLogin);
        senhaAnonima = findViewById(R.id.editTextSenhaAnonimaLogin);

        btnLogin = findViewById(R.id.btnLogin);
        checkAnonimo = findViewById(R.id.checkAnonimo);
        layoutNormal = findViewById(R.id.layoutNormalLogin);
        layoutAnonimo = findViewById(R.id.layoutAnonimoLogin);

        // Alterna os layouts
        checkAnonimo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutNormal.setVisibility(View.GONE);
                layoutAnonimo.setVisibility(View.VISIBLE);
            } else {
                layoutNormal.setVisibility(View.VISIBLE);
                layoutAnonimo.setVisibility(View.GONE);
            }
        });

        btnLogin.setOnClickListener(v -> {
            if (checkAnonimo.isChecked()) {
                loginAnonimo();
            } else {
                loginNormal();
            }
        });
    }

    private void loginNormal() {
        String email2 = email.getText().toString().trim();
        String senha2 = senha.getText().toString().trim();

        if (email2.isEmpty() || senha2.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email2, senha2)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(this, TelaDeRelatos.class));
                         finish();
                    } else {
                        Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginAnonimo() {
        String cod = codinome.getText().toString().trim();
        String senhaA = senhaAnonima.getText().toString().trim();

        if (cod.isEmpty() || senhaA.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuaria")
                .whereEqualTo("nome", cod) // ou "codinome" dependendo do atributo salvo
                .whereEqualTo("senha", senhaA)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Toast.makeText(this, "Login anônimo realizado com sucesso!", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(this, TelaDeRelatos.class));
                         finish();
                    } else {
                        Toast.makeText(this, "Codinome ou senha incorretos.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
