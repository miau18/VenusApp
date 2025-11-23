package com.example.vnus.ui.auth.psicologo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.data.model.Psicologo;
import com.example.vnus.data.model.Users;
import com.example.vnus.data.repository.PsicologoRepository;
import com.example.vnus.data.repository.UsersRepository;
import com.example.vnus.ui.auth.usuaria.CadastroUsuaria;
import com.example.vnus.ui.auth.usuaria.LoginUsuaria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class CadastroPsicologo extends AppCompatActivity {

    private UsersRepository ur;
    private FirebaseAuth auth;
    private PsicologoRepository pr;
    private EditText nomeEditText, sobrenomeEditText, emailEditText, crpEditText, senhaEditText;
    private Button btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_psicologo);

        auth = FirebaseAuth.getInstance();
        ur = new UsersRepository();
        pr = new PsicologoRepository();

        nomeEditText = findViewById(R.id.editTextName);
        sobrenomeEditText = findViewById(R.id.editTextSobrenome);
        emailEditText = findViewById(R.id.editTextEmail);
        crpEditText = findViewById(R.id.editTextCrp);
        senhaEditText = findViewById(R.id.editTextSenha);
        btnCadastro = findViewById(R.id.buttonRegister);

        btnCadastro.setOnClickListener(v -> {

            String nome = nomeEditText.getText().toString().trim();
            String sobrenome = sobrenomeEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String crp = crpEditText.getText().toString().trim();
            String senha = senhaEditText.getText().toString().trim();

            if (nome.isEmpty() || sobrenome.isEmpty() || email.isEmpty() || crp.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser == null) return;

                            String userId = firebaseUser.getUid();

                            Users user = new Users(userId, nome, sobrenome, email, "psicologo");
                            Psicologo psicologo = new Psicologo(userId, nome, sobrenome, email, crp);

                            ur.saveUser(user,
                                    aVoid -> {
                                        pr.savePsicologoSpecificData(psicologo,
                                                aVoid1 -> {
                                                    Toast.makeText(this, "Psicólogo registrado com sucesso!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(this, LoginPsicologo.class));
                                                    finish();
                                                },
                                                e -> Toast.makeText(this, "Erro ao salvar dados do psicólogo: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                                    },
                                    e -> Toast.makeText(this, "Erro ao salvar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );

                        } else {
                            String msg = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                            Toast.makeText(this, "Erro no registro: " + msg, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}