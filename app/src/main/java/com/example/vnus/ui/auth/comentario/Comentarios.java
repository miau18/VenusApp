package com.example.vnus.ui.auth.comentario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Comentario;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.model.Users;
import com.example.vnus.data.repository.ComentarioRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.psicologo.PerfilPsicologo;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Comentarios extends AppCompatActivity {

    private RecyclerView recyclerComentario;
    private EditText editTextComentario;
    private Button btnPublicar;
    private ComentarioRepository cr;
    private ComentarioAdapter ca;
    private List<Comentario> comentarios;
    private String relatoId;
    private Users usuarioAtual;
    private Usuaria usuariaAtual;
    private ListenerRegistration cl;
    private ImageView home, informacoes, perfilUsuarioLogado, chat, lupa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_comentario);

        relatoId = getIntent().getStringExtra("relatoId");

        recyclerComentario = findViewById(R.id.recyclerComentarios);
        editTextComentario = findViewById(R.id.editTextComentario);
        btnPublicar = findViewById(R.id.btnPublicar);

        cr = new ComentarioRepository();
        comentarios = new ArrayList<>();
        ca = new ComentarioAdapter(comentarios);
        recyclerComentario.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentario.setAdapter(ca);

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

        carregarUsuarioAtual();
        carregarComentarios();

        btnPublicar.setOnClickListener(v -> publicarComentario());
    }

    private void carregarUsuarioAtual() {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fbUser != null) {
            if (fbUser.isAnonymous()) {
                SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
                String codinome = prefs.getString("codinome_real", "Anônima");
                String idBanco = prefs.getString("id_banco_real", fbUser.getUid());

                usuarioAtual = new Users();
                usuarioAtual.setId(idBanco);
                usuarioAtual.setNome(codinome);

                usuariaAtual = new Usuaria();
                usuariaAtual.setId(idBanco);
                usuariaAtual.setAnonima(true);
                usuariaAtual.setCodinome(codinome);
                return;
            }

            String uid = fbUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuaria").document(uid).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Usuaria u = doc.toObject(Usuaria.class);
                            if(u != null){
                                String nome = (u.getNome() != null ? u.getNome() : "") + " " + (u.getSobrenome() != null ? u.getSobrenome() : "");
                                usuarioAtual = new Users();
                                usuarioAtual.setId(uid);
                                usuarioAtual.setNome(nome.trim());
                            }
                        } else {
                            db.collection("psicologo").document(uid).get()
                                    .addOnSuccessListener(docPsi -> {
                                        if(docPsi.exists()){
                                            String nome = docPsi.getString("nome") + " " + docPsi.getString("sobrenome") + " (Psicólogo)";
                                            usuarioAtual = new Users();
                                            usuarioAtual.setId(uid);
                                            usuarioAtual.setNome(nome);
                                        }
                                    });
                        }
                    });
        }
    }

    private void carregarComentarios(){
        cl = cr.listenComentarios(relatoId, (snapshots, e) -> {
            if(e != null) return;
            comentarios.clear();
            if(snapshots != null){
                comentarios.addAll(snapshots.toObjects(Comentario.class));
                ca.notifyDataSetChanged();
            }
        });
    }

    public void publicarComentario(){
        String conteudo = editTextComentario.getText().toString().trim();
        if(conteudo.isEmpty()) return;

        if(usuarioAtual == null){
            Toast.makeText(this, "Carregando perfil...", Toast.LENGTH_SHORT).show();
            return;
        }

        Comentario c = new Comentario(UUID.randomUUID().toString(), conteudo, new Date());
        c.setUser(usuarioAtual);

        comentarios.add(0, c);
        ca.notifyItemInserted(0);
        recyclerComentario.scrollToPosition(0);

        cr.saveComentarioSpecificData(relatoId, c,
                unused -> editTextComentario.setText(""),
                e -> Toast.makeText(this, "Erro ao publicar", Toast.LENGTH_SHORT).show());
    }

    @Override protected void onDestroy() { super.onDestroy(); if(cl != null) cl.remove(); }
    private void buscarPerfilCorreto() {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuaria").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) startActivity(new Intent(this, PerfilUsuaria.class));
            else
                db.collection("psicologo").document(uid).get().addOnSuccessListener(d -> startActivity(new Intent(this, PerfilPsicologo.class)));
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