package com.example.vnus.ui.auth.psicologo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPsicologo extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, senha;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_psicologo);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextEmail4);
        senha = findViewById(R.id.editTextSenha4);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginPsicologo());
    }

    private void loginPsicologo() {
        String emailStr = email.getText().toString().trim();
        String senhaStr = senha.getText().toString().trim();

        if(emailStr.isEmpty() || senhaStr.isEmpty()){
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(emailStr, senhaStr)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.putString("tipo_usuario", "psicologo");
                        editor.apply();

                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(this, TelaDeRelatosPsicologo.class);
                        i.putExtra("tipoUsuario", "psicologo");
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}