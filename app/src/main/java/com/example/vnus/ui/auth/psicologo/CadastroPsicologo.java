package com.example.vnus.ui.auth.psicologo;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class CadastroPsicologo extends AppCompatActivity {

    private UsersRepository ur;
    private FirebaseAuth auth;
    private PsicologoRepository pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_psicologo);

        auth = FirebaseAuth.getInstance();
        ur = new UsersRepository();
        pr = new PsicologoRepository();

        EditText nomeEditText = findViewById(R.id.editTextName);
        EditText sobrenomeEditText = findViewById(R.id.editTextSobrenome);
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText crpEditText = findViewById(R.id.editTextCrp);
        EditText senhaEditText = findViewById(R.id.editTextSenha);
        Button btnCadastro = findViewById(R.id.buttonRegister);

        btnCadastro.setOnClickListener(v -> {
            String nome = nomeEditText.getText().toString().trim();
            String sobrenome = sobrenomeEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String crp = crpEditText.getText().toString().trim();
            String senha = senhaEditText.getText().toString().trim();

            if (nome.isEmpty() || sobrenome.isEmpty() || email.isEmpty() || crp.isEmpty() || senha.isEmpty()) {
                Toast.makeText(CadastroPsicologo.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                Users users = new Users(userId, nome, sobrenome, email, "psicologo");
                                Psicologo psicologo = new Psicologo(userId, nome, sobrenome, email, crp);

                                // Salva o usuário na coleção 'users' e depois na coleção 'psicologo'
                                ur.saveUser(users,
                                        aVoid -> {
                                            pr.savePsicologoSpecificData(psicologo,
                                                    aVoid1 -> Toast.makeText(CadastroPsicologo.this, "Psicólogo registrado com sucesso!", Toast.LENGTH_SHORT).show(),
                                                    e -> Toast.makeText(CadastroPsicologo.this, "Erro ao salvar dados do psicólogo", Toast.LENGTH_SHORT).show()
                                            );
                                        },
                                        e -> Toast.makeText(CadastroPsicologo.this, "Erro ao salvar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(CadastroPsicologo.this, "O e-mail informado já está em uso.", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(CadastroPsicologo.this, "A senha é muito fraca. Ela deve conter no mínimo 6 caracteres.", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(CadastroPsicologo.this, "O formato do e-mail é inválido.", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(CadastroPsicologo.this, "Erro no registro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }
}