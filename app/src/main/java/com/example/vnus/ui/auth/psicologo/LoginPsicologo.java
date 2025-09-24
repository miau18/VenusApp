package com.example.vnus.ui.auth.psicologo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPsicologo extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, senha;
    private Button btnLogin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_psicologo);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.editTextEmail4);
        senha = findViewById(R.id.editTextSenha4);
        btnLogin = findViewById(R.id.btnLogin);


    }
}
