package com.example.vnus.ui.auth.pesquisa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.RelatoRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.psicologo.PerfilPsicologo;
import com.example.vnus.ui.auth.relato.RelatoAdapter;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TelaPesquisa extends AppCompatActivity {

    private EditText editPesquisa;
    private RecyclerView recycler;
    private List<Usuaria> listaUsuariasEncontradas = new ArrayList<>();
    private List<Relato> listaRelatosEncontrados = new ArrayList<>();
    private PesquisaAdapter adapterUsuarios;
    private RelatoAdapter adapterRelatos;
    private ConcatAdapter concatAdapter;
    private RelatoRepository rr;
    private ImageView perfilUsuarioLogado, informacoes, chat, home;
    private final Set<String> addedIds = new HashSet<>();
    private String meuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_pesquisa);

        meuId = FirebaseAuth.getInstance().getUid();
        rr = new RelatoRepository();

        editPesquisa = findViewById(R.id.editTextPesquisa);
        recycler = findViewById(R.id.recyclerResultados);

        adapterUsuarios = new PesquisaAdapter(this, listaUsuariasEncontradas);
        adapterRelatos = new RelatoAdapter(listaRelatosEncontrados, this, rr);
        concatAdapter = new ConcatAdapter(adapterUsuarios, adapterRelatos);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(concatAdapter);

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

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            abrirHomeCorreto();
        });

        perfilUsuarioLogado = findViewById(R.id.config_icon);
        perfilUsuarioLogado.setOnClickListener(v -> {
            buscarPerfilCorreto();
        });

        editPesquisa.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                buscar(s.toString());
            }
        });
    }

    private void buscar(String texto) {
        listaUsuariasEncontradas.clear();
        listaRelatosEncontrados.clear();
        addedIds.clear();

        adapterUsuarios.notifyDataSetChanged();
        adapterRelatos.notifyDataSetChanged();

        if (texto == null || texto.trim().isEmpty()) return;

        final String termo = texto.toLowerCase().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuaria").get().addOnSuccessListener(query -> {
            String textoAtual = editPesquisa.getText().toString().toLowerCase().trim();
            if (!textoAtual.equals(termo)) return;

            for (QueryDocumentSnapshot doc : query) {
                if (doc.getId().equals(meuId)) continue;

                Usuaria u = doc.toObject(Usuaria.class);
                u.setId(doc.getId());

                boolean match = false;

                if (Boolean.TRUE.equals(u.getAnonima())) {
                    if (u.getCodinome() != null && u.getCodinome().toLowerCase().contains(termo)) {
                        match = true;
                    }
                } else {
                    String nome = u.getNome() != null ? u.getNome() : "";
                    String sobrenome = u.getSobrenome() != null ? u.getSobrenome() : "";
                    String nomeCompleto = (nome + " " + sobrenome).toLowerCase();

                    if (nomeCompleto.contains(termo)) match = true;
                }

                if (match && !addedIds.contains("USER_" + u.getId())) {
                    listaUsuariasEncontradas.add(u);
                    addedIds.add("USER_" + u.getId());
                }
            }
            adapterUsuarios.notifyDataSetChanged();
        });

        db.collection("relato").get().addOnSuccessListener(query -> {
            String textoAtual = editPesquisa.getText().toString().toLowerCase().trim();
            if (!textoAtual.equals(termo)) return;

            for (DocumentSnapshot doc : query.getDocuments()) {
                try {
                    Relato relato = doc.toObject(Relato.class);
                    if (relato == null) continue;
                    relato.setId(doc.getId());

                    boolean match = false;

                    if (relato.getConteudo() != null && relato.getConteudo().toLowerCase().contains(termo)) {
                        match = true;
                    }

                    if (!match && relato.getHospital() != null && relato.getHospital().getNome() != null) {
                        if (relato.getHospital().getNome().toLowerCase().contains(termo)) match = true;
                    }

                    if (!match && relato.getUsuaria() != null) {
                        Usuaria u = relato.getUsuaria();
                        if (Boolean.TRUE.equals(u.getAnonima())) {
                            if (u.getCodinome() != null && u.getCodinome().toLowerCase().contains(termo)) match = true;
                        } else {
                            String nome = u.getNome() != null ? u.getNome() : "";
                            String sobrenome = u.getSobrenome() != null ? u.getSobrenome() : "";
                            String nomeCompleto = (nome + " " + sobrenome).toLowerCase();
                            if (nomeCompleto.contains(termo)) match = true;
                        }
                    }

                    if (match && !addedIds.contains("RELATO_" + relato.getId())) {
                        listaRelatosEncontrados.add(relato);
                        addedIds.add("RELATO_" + relato.getId());
                    }
                } catch (Exception e) {}
            }
            adapterRelatos.notifyDataSetChanged();
        });
    }

    private void buscarPerfilCorreto() {
        SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
        String tipoUsuario = prefs.getString("tipo_usuario", null);

        if ("usuaria".equals(tipoUsuario)) {
            startActivity(new Intent(this, PerfilUsuaria.class));
            finish();
            overridePendingTransition(0, 0);
            return;
        } else if ("psicologo".equals(tipoUsuario)) {
            startActivity(new Intent(this, PerfilPsicologo.class));
            finish();
            overridePendingTransition(0, 0);
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuaria").document(uid).get().addOnSuccessListener(docUsuaria -> {
            if (docUsuaria.exists()) {
                prefs.edit().putString("tipo_usuario", "usuaria").apply();
                startActivity(new Intent(this, PerfilUsuaria.class));
            } else {
                db.collection("psicologo").document(uid).get().addOnSuccessListener(docPsicologo -> {
                    if (docPsicologo.exists()) {
                        prefs.edit().putString("tipo_usuario", "psicologo").apply();
                        startActivity(new Intent(this, PerfilPsicologo.class));
                    }
                });
            }
            finish();
            overridePendingTransition(0, 0);
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