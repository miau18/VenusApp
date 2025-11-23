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
    private EditText nomeEditText, sobrenomeEditText, emailEditText, codinomeEditText, senhaAnonimaEditText, senhaEditText;
    private Button btnCadastro;
    private CheckBox checkAnonimo;
    private LinearLayout layoutNormal, layoutAnonimo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_usuaria);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        ur = new UsersRepository();
        usuariaR = new UsuariaRepository();

        nomeEditText = findViewById(R.id.editTextName2);
        sobrenomeEditText = findViewById(R.id.editTextSobrenome2);
        emailEditText = findViewById(R.id.editTextEmail2);
        senhaEditText = findViewById(R.id.editTextSenha2);
        btnCadastro = findViewById(R.id.btnCadastro);
        checkAnonimo = findViewById(R.id.checkAnonimo);
        codinomeEditText = findViewById(R.id.editTextCodinome);
        senhaAnonimaEditText = findViewById(R.id.editTextSenhaAnonima);
        layoutNormal = findViewById(R.id.layoutNormal);
        layoutAnonimo = findViewById(R.id.layoutAnonimo);

        if (checkAnonimo.isChecked()) {
            layoutNormal.setVisibility(View.GONE);
            layoutAnonimo.setVisibility(View.VISIBLE);
        } else {
            layoutNormal.setVisibility(View.VISIBLE);
            layoutAnonimo.setVisibility(View.GONE);
        }

        checkAnonimo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "checkAnonimo changed: " + isChecked);
            if (isChecked) {
                layoutNormal.setVisibility(View.GONE);
                layoutAnonimo.setVisibility(View.VISIBLE);
            } else {
                layoutNormal.setVisibility(View.VISIBLE);
                layoutAnonimo.setVisibility(View.GONE);
            }
        });

        btnCadastro.setOnClickListener(view -> {
            if (checkAnonimo.isChecked()) {
                // --- CADASTRO ANÔNIMO ---
                String codinome = codinomeEditText.getText().toString().trim();
                String senhaAnonimo = senhaAnonimaEditText.getText().toString().trim();

                if (codinome.isEmpty() || senhaAnonimo.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = db.collection("usuaria").document().getId();

                Users userAnonimo = new Users(userId, codinome, "", "", "usuaria");

                Usuaria usuariaAnonima = new Usuaria(userId, codinome, senhaAnonimo);

                ur.saveUser(userAnonimo,
                        aVoid -> {
                            usuariaR.saveUsuariaSpecifiData(usuariaAnonima,
                                    aVoid1 -> {
                                        Toast.makeText(this, "Usuária anônima registrada com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CadastroUsuaria.this, LoginUsuaria.class);
                                        startActivity(intent);
                                        finish();
                                    },
                                    e -> Toast.makeText(this, "Erro ao salvar dados específicos: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        },
                        e -> Toast.makeText(this, "Erro ao criar usuário base: " + e.getMessage(), Toast.LENGTH_SHORT).show()
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