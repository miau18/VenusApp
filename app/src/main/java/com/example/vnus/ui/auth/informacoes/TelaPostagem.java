package com.example.vnus.ui.auth.informacoes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.psicologo.PerfilPsicologo;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TelaPostagem extends AppCompatActivity {

    private ImageView imgTema, home, perfilUsuarioLogado, lupa, informacoes, chat;
    private TextView txtTitulo, txtDescricao;
    private Button btnAnterior, btnProxima;
    private ScrollView scrollConteudo;

    private List<TemaInformacoes> listaTemas;
    private int posicaoAtual = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacoes);

        imgTema = findViewById(R.id.imgTema);
        txtTitulo = findViewById(R.id.textViewTituloTema);
        txtDescricao = findViewById(R.id.textViewDescricaoTema);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnProxima = findViewById(R.id.btnProxima);
        scrollConteudo = findViewById(R.id.scrollConteudo);

        listaTemas = new ArrayList<>();
        carregarDadosLista();

        Intent i = getIntent();
        if (i != null) {
            posicaoAtual = i.getIntExtra("posicao_atual", 0);
        }

        atualizarInterface();

        btnProxima.setOnClickListener(v -> {
            if (posicaoAtual < listaTemas.size() - 1) {
                posicaoAtual++;
                atualizarInterface();
            }
        });

        btnAnterior.setOnClickListener(v -> {
            if (posicaoAtual > 0) {
                posicaoAtual--;
                atualizarInterface();
            }
        });

        configurarNavegacao();
    }

    private void atualizarInterface() {
        if (listaTemas.isEmpty()) return;

        TemaInformacoes tema = listaTemas.get(posicaoAtual);

        txtTitulo.setText(tema.getTitulo());
        txtDescricao.setText(tema.getDescricao());
        imgTema.setImageResource(tema.getImagem());

        if (posicaoAtual == 0) {
            btnAnterior.setVisibility(View.INVISIBLE);
        } else {
            btnAnterior.setVisibility(View.VISIBLE);
        }

        if (posicaoAtual == listaTemas.size() - 1) {
            btnProxima.setVisibility(View.INVISIBLE);
        } else {
            btnProxima.setVisibility(View.VISIBLE);
        }

        scrollConteudo.fullScroll(ScrollView.FOCUS_UP);
    }

    private void carregarDadosLista() {
        listaTemas.add(new TemaInformacoes(
                getString(R.string.tema_episiotomia_titulo),
                getString(R.string.descricao_tema_episiotomia),
                R.drawable.obstetric
        ));

        listaTemas.add(new TemaInformacoes(
                getString(R.string.tema_manobra_tirulo),
                getString(R.string.descricao_tema_manobra),
                R.drawable.obstetric
        ));

        listaTemas.add(new TemaInformacoes(
                getString(R.string.tema_parto_titulo),
                getString(R.string.descricao_tema_restricoes),
                R.drawable.obstetric
        ));

        listaTemas.add(new TemaInformacoes(
                getString(R.string.tema_acompanhante_titulo),
                getString(R.string.descricao_tema_acompanhante),
                R.drawable.obstetric
        ));

        listaTemas.add(new TemaInformacoes(
                getString(R.string.tema_denuncia_titulo),
                getString(R.string.descricao_tema_denuncia),
                R.drawable.obstetric
        ));
    }

    private void configurarNavegacao() {
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

        perfilUsuarioLogado = findViewById(R.id.config_icon);
        perfilUsuarioLogado.setOnClickListener(v -> {
            buscarPerfilCorreto();
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
    }

    private void buscarPerfilCorreto(){
        String uid = FirebaseAuth.getInstance().getUid();
        if(uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuaria").document(uid).get()
                .addOnSuccessListener(docUsuaria -> {
                    if(docUsuaria.exists()){
                        startActivity(new Intent(this, PerfilUsuaria.class));
                    }else{
                        db.collection("psicologo").document(uid).get()
                                .addOnSuccessListener(docPsicologo -> {
                                    if(docPsicologo.exists()){
                                        startActivity(new Intent(this, PerfilPsicologo.class));
                                    }else{
                                        Toast.makeText(this, "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
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