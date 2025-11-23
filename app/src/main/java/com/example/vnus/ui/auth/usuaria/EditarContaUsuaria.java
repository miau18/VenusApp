package com.example.vnus.ui.auth.usuaria;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.data.repository.UsuariaRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.home.TelaInicial;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.google.firebase.auth.FirebaseAuth;

public class EditarContaUsuaria extends AppCompatActivity {

    private EditText editEmailOuCodinome, editSenha, editSenhaAtual;
    private Button btnSalvar, btnExcluir;
    private boolean isAnonima;
    private UsuariaRepository ur;
    private String idUsuaria;
    private ImageView home, informacoes, perfilUsuaria, chat, lupa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_conta);

        ur = new UsuariaRepository();

        SharedPreferences sp = getSharedPreferences("DadosUsuario", Context.MODE_PRIVATE);
        String codinomeReal = sp.getString("codinome_real", null);
        isAnonima = (codinomeReal != null);

        idUsuaria = ur.getUsuariaLogadaId(this);

        if (idUsuaria == null) {
            Toast.makeText(this, "Erro ao identificar usuária.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        perfilUsuaria = findViewById(R.id.config_icon);
        perfilUsuaria.setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilUsuaria.class));
        });

        informacoes = findViewById(R.id.informacoes);
        informacoes.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaInformacoes.class));
        });

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            Intent i = new Intent(this, TelaDeRelatos.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        });

        chat = findViewById(R.id.iconChat);
        chat.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaChat.class));
        });

        lupa = findViewById(R.id.lupa);
        lupa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
        });

        editEmailOuCodinome = findViewById(R.id.editTextEmail5);
        editSenhaAtual = findViewById(R.id.editTextSenhaAtual);
        editSenha = findViewById(R.id.editTextSenha5);
        btnSalvar = findViewById(R.id.btnEditarCadastro);
        btnExcluir = findViewById(R.id.btnExcluir);


        btnSalvar.setOnClickListener(v -> {
            String novoEmailOuCodinome = editEmailOuCodinome.getText().toString().trim();
            String senhaAtual = editSenhaAtual.getText().toString().trim();
            String novaSenha = editSenha.getText().toString().trim();

            if (novoEmailOuCodinome.isEmpty() || novaSenha.isEmpty() || (!isAnonima && senhaAtual.isEmpty())) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            ur.editarCadastro(
                    idUsuaria,
                    novoEmailOuCodinome,
                    senhaAtual,
                    novaSenha,
                    isAnonima,
                    aVoid -> {
                        Toast.makeText(this, "Dados atualizados com sucesso, faça login novamente!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginUsuaria.class));
                        if (isAnonima) {
                            sp.edit().putString("codinome_real", novoEmailOuCodinome).apply();
                        }
                        finish();
                    },
                    e -> {
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("EditarConta", "Erro: ", e);
                    }
            );
        });

        btnExcluir.setOnClickListener(v -> confirmarExclusao());
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir conta")
                .setMessage("Tem certeza que deseja excluir esta conta permanentemente?")
                .setPositiveButton("Sim", (dialog, which) -> excluirConta())
                .setNegativeButton("Não", null)
                .show();
    }

    private void excluirConta() {
        ur.excluirConta(
                idUsuaria,
                isAnonima,
                aVoid -> {
                    Toast.makeText(this, "Conta excluída.", Toast.LENGTH_SHORT).show();

                    SharedPreferences sp = getSharedPreferences("DadosUsuario", Context.MODE_PRIVATE);
                    sp.edit().clear().apply();

                    if (!isAnonima) {
                        FirebaseAuth.getInstance().signOut();
                    }

                    Intent i = new Intent(this, TelaInicial.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                },
                e -> Toast.makeText(this, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}