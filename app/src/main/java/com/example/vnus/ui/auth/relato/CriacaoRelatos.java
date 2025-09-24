package com.example.vnus.ui.auth.relato;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.RelatoRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class CriacaoRelatos extends AppCompatActivity {

    private EditText criarRelato;
    private Button btnPublicar;
    private RelatoRepository rr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criacao_relato);

        criarRelato = findViewById(R.id.editTextCriarRelato);
        btnPublicar = findViewById(R.id.btnPublicar);
        rr = new RelatoRepository();

        btnPublicar.setOnClickListener(v -> {
            String conteudo = criarRelato.getText().toString().trim();

            if(conteudo.isEmpty()){
                Toast.makeText(this, "Digite seu relato", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = UUID.randomUUID().toString();
            Relato relato = new Relato(id, conteudo, false, new Date(), 0);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                FirebaseFirestore.getInstance().collection("usuaria")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(snapshot ->{
                            Usuaria usuaria = snapshot.toObject(Usuaria.class);
                            relato.setUsuaria(usuaria);

                            rr.saveRelato(relato,
                                    unused -> {
                                Toast.makeText(this, "Relato publicado", Toast.LENGTH_SHORT).show();
                                finish();
                                    },
                                    e -> Toast.makeText(this, "Erro ao publicar" + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Erro ao carregar usuária" + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            } else{
                Toast.makeText(this, "Usuária não autenticada", Toast.LENGTH_SHORT).show();
            }

            rr.saveRelato(relato,
                   unused -> {
                        Toast.makeText(this, "Relato publicado", Toast.LENGTH_SHORT).show();
                        finish();
                   },
                    e -> Toast.makeText(this, "Erro ao publicar" + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}