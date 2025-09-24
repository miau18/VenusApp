package com.example.vnus.ui.auth.usuaria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.data.model.Users;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.UsersRepository;
import com.example.vnus.data.repository.UsuariaRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CadastroUsuaria extends AppCompatActivity {

    private static final String TAG = "CadastroUsuaria";

    private FirebaseAuth auth;
    private UsersRepository ur;
    private UsuariaRepository usuariaR;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_usuaria);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        ur = new UsersRepository();
        usuariaR = new UsuariaRepository();

        EditText nomeEditText = findViewById(R.id.editTextName2);
        EditText sobrenomeEditText = findViewById(R.id.editTextSobrenome2);
        EditText emailEditText = findViewById(R.id.editTextEmail2);
        EditText senhaEditText = findViewById(R.id.editTextSenha2);
        Button btnCadastro = findViewById(R.id.btnCadastro);
        CheckBox checkAnonimo = findViewById(R.id.checkAnonimo);
        EditText codinomeEditText = findViewById(R.id.editTextCodinome);
        EditText senhaAnonimaEditText = findViewById(R.id.editTextSenhaAnonima);
        LinearLayout layoutNormal = findViewById(R.id.layoutNormal);
        LinearLayout layoutAnonimo = findViewById(R.id.layoutAnonimo);

        if (checkAnonimo.isChecked()) {
            layoutNormal.setVisibility(View.GONE);
            layoutAnonimo.setVisibility(View.VISIBLE);
        } else {
            layoutNormal.setVisibility(View.VISIBLE);
            layoutAnonimo.setVisibility(View.GONE);
        }

        checkAnonimo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "checkAnonimo changed: " + isChecked);
                if (isChecked) {
                    layoutNormal.setVisibility(View.GONE);
                    layoutAnonimo.setVisibility(View.VISIBLE);
                } else {
                    layoutNormal.setVisibility(View.VISIBLE);
                    layoutAnonimo.setVisibility(View.GONE);
                }
            }
        });

        btnCadastro.setOnClickListener(view -> {
            if (checkAnonimo.isChecked()) {
                String codinome = codinomeEditText.getText().toString().trim();
                String senhaAnonimo = senhaAnonimaEditText.getText().toString().trim();

                if (codinome.isEmpty() || senhaAnonimo.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = db.collection("usuaria").document().getId();
                Usuaria usuariaAnonima = new Usuaria(userId, codinome, senhaAnonimo);

                usuariaR.saveUsuariaSpecifiData(usuariaAnonima,
                        aVoid -> {
                            Toast.makeText(this, "Usuária anônima registrada com sucesso!", Toast.LENGTH_SHORT).show();
                            // opcional: redirecionar para LoginUsuaria
                            Intent intent = new Intent(CadastroUsuaria.this, LoginUsuaria.class);
                            startActivity(intent);
                            finish();
                        },
                        e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

            } else {
                String nome = nomeEditText.getText().toString().trim();
                String sobrenome = sobrenomeEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String senha = senhaEditText.getText().toString().trim();

                if (nome.isEmpty() || sobrenome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(CadastroUsuaria.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String userId = firebaseUser.getUid();

                                    Users user = new Users(userId, nome, sobrenome, email, "usuaria");
                                    Usuaria usuaria = new Usuaria(userId, nome, sobrenome, email);

                                    ur.saveUser(user,
                                            aVoid -> {
                                                usuariaR.saveUsuariaSpecifiData(usuaria,
                                                        aVoid1 -> {
                                                            Toast.makeText(CadastroUsuaria.this, "Usuária registrada com sucesso!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(CadastroUsuaria.this, LoginUsuaria.class);
                                                            startActivity(intent);
                                                            finish();
                                                        },
                                                        e -> Toast.makeText(CadastroUsuaria.this, "Erro ao salvar dados da usuária: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                            },
                                            e -> Toast.makeText(CadastroUsuaria.this, "Erro ao salvar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                                }
                            } else {
                                String msg = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                                Toast.makeText(CadastroUsuaria.this, "Erro no registro: " + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
