package com.example.vnus.ui.auth.psicologo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.data.repository.PsicologoRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.home.TelaInicial;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarContaPsicologo extends AppCompatActivity {

    private EditText editEmail, editSenha, editSenhaAtual;
    private Button btnSalvar, btnExcluir;
    private PsicologoRepository pr;
    private String idPsicologo;
    private ImageView home, informacoes, perfilUsuario, chat, pesquisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_conta_psicologo);

        pr = new PsicologoRepository();

        if (getIntent().hasExtra("psicologoId")) {
            idPsicologo = getIntent().getStringExtra("psicologoId");
        } else {
            idPsicologo = pr.getPsicologoLogadoId(this);
        }

        if(idPsicologo == null || idPsicologo.equals("Nenhum usuário")){
            Toast.makeText(this, "Erro ao identificar psicólogo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        perfilUsuario = findViewById(R.id.config_icon);
        perfilUsuario.setOnClickListener(v -> {
            finish();
        });

        informacoes = findViewById(R.id.informacoes);
        informacoes.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaInformacoes.class));
        });

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            abrirHomeCorreto();
            finish();
        });

        chat = findViewById(R.id.iconChat);
        chat.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaChat.class));
            finish();
        });

        pesquisa= findViewById(R.id.lupa);
        pesquisa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
            finish();
        });

        editEmail = findViewById(R.id.editTextEmail5);
        editSenhaAtual = findViewById(R.id.editTextSenhaAtual);
        editSenha = findViewById(R.id.editTextSenha5);
        btnSalvar = findViewById(R.id.btnEditarCadastro);
        btnExcluir = findViewById(R.id.btnExcluir);

        btnSalvar.setOnClickListener(v -> {
            String novoEmail = editEmail.getText().toString().trim();
            String senhaAtual = editSenhaAtual.getText().toString().trim();
            String novaSenha = editSenha.getText().toString().trim();

            if(novoEmail.isEmpty() || novaSenha.isEmpty() || senhaAtual.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            pr.editarConta(idPsicologo, novoEmail, senhaAtual, novaSenha,
                    aVoid -> {
                        Toast.makeText(this, "Dados atualizados! Faça login novamente.", Toast.LENGTH_SHORT).show();
                        pr.logoutPsicologo(this);
                        Intent intent = new Intent(this, LoginPsicologo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }, e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        btnExcluir.setOnClickListener(v -> confirmarExclusao());
    }

    public void confirmarExclusao(){
        new AlertDialog.Builder(this)
                .setTitle("Excluir conta")
                .setMessage("Tem certeza que deseja excluir esta conta permanentemente?")
                .setPositiveButton("Sim", (dialog, which) -> excluirConta())
                .setNegativeButton("Não", null)
                .show();
    }

    private void excluirConta(){
        pr.excluirConta(idPsicologo, aVoid -> {
            Toast.makeText(this, "Conta excluída.", Toast.LENGTH_SHORT).show();
            SharedPreferences sp = getSharedPreferences("DadosUsuario", Context.MODE_PRIVATE);
            sp.edit().clear().apply();

            Intent i = new Intent(this, TelaInicial.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }, e -> Toast.makeText(this, "Erro ao excluir conta: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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