package com.example.vnus.ui.auth.psicologo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.data.model.Psicologo;
import com.example.vnus.data.repository.PsicologoRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilPsicologo extends AppCompatActivity {

    private TextView textNome, textEmail;
    private Button btnEditar, btnSair;
    private PsicologoRepository pr;
    private Psicologo psicologoAtual;
    private ImageView home, informacoes, chat, lupa;
    private String idPsicologoLogado;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_psicologo);

        textNome = findViewById(R.id.textViewNome2);
        textEmail = findViewById(R.id.textViewEmail2);
        btnEditar = findViewById(R.id.btnEditarPerfil2);
        btnSair = findViewById(R.id.btnSair2);

        pr = new PsicologoRepository();
        idPsicologoLogado = pr.getPsicologoLogadoId(this);

        chat = findViewById(R.id.iconChat);
        chat.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaChat.class));
            finish();
        });

        informacoes = findViewById(R.id.informacoes);
        informacoes.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaInformacoes.class));
            finish();
        });

        lupa = findViewById(R.id.lupa);
        lupa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
            finish();
        });

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            abrirHomeCorreto();
            finish();
        });

        btnEditar.setOnClickListener(v -> {
            if (idPsicologoLogado != null && !idPsicologoLogado.equals("Nenhum usuário")) {
                Intent i = new Intent(this, EditarContaPsicologo.class);
                i.putExtra("psicologoId", idPsicologoLogado);
                startActivity(i);
            } else {
                Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSair.setOnClickListener(v -> {
            pr.logoutPsicologo(this);
            Toast.makeText(this, "Logout realizado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPsicologo.class));
            finish();
        });

        carregarDadosPsicolog();
    }

    private void carregarDadosPsicolog() {
        if (idPsicologoLogado == null || idPsicologoLogado.equals("Nenhum usuário")) {
            Toast.makeText(this, "Nenhum psicólogo logado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pr.getPsicologoPorId(idPsicologoLogado, psicologo -> {
            psicologoAtual = psicologo;
            textNome.setText(psicologo.getNome() + " " + psicologo.getSobrenome());
            textEmail.setText(psicologo.getEmail());

        }, e -> Toast.makeText(this, "Erro ao carregar dados do psicólogo", Toast.LENGTH_SHORT).show());
    }

    public void abrirHomeCorreto(){
        SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
        String tipoUsuario = prefs.getString("tipo_usuario", null);

        if ("usuaria".equals(tipoUsuario)) {
            Intent intent = new Intent(this, TelaDeRelatos.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
            return;
        } else if ("psicologo".equals(tipoUsuario)) {
            Intent intent = new Intent(this, TelaDeRelatosPsicologo.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuaria").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        prefs.edit().putString("tipo_usuario", "usuaria").apply();
                        Intent intent = new Intent(this, TelaDeRelatos.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        db.collection("psicologo").document(uid).get()
                                .addOnSuccessListener(docPsi -> {
                                    if(docPsi.exists()){
                                        prefs.edit().putString("tipo_usuario", "psicologo").apply();
                                        Intent intent = new Intent(this, TelaDeRelatosPsicologo.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    } else {
                                        Toast.makeText(this, "Perfil não encontrado", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao verificar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}