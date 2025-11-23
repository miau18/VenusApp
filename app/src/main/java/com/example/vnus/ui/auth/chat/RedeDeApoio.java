package com.example.vnus.ui.auth.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // Importante para debug
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Conversa;
import com.example.vnus.data.model.Users;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.psicologo.PerfilPsicologo;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RedeDeApoio extends AppCompatActivity {

    private RecyclerView recycler;
    private ConversaAdapter adapter;
    private List<Conversa> listaRedeApoio = new ArrayList<>();
    private List<String> redeApoioIds = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioAtualId;
    private ListenerRegistration conversasListener;
    private final CollectionReference conversasRef = db.collection("conversas");
    private TextView telaConversas;
    private ImageView home, lupa, informacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rede_de_apoio);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isAnonymous()) {
                SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
                usuarioAtualId = prefs.getString("id_banco_real", null);
                if(usuarioAtualId == null) usuarioAtualId = currentUser.getUid();
            } else {
                usuarioAtualId = currentUser.getUid();
            }
            Log.d("RedeDeApoio", "ID Usado: " + usuarioAtualId);
        } else {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recycler = findViewById(R.id.recyclerRedeApoio);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        telaConversas = findViewById(R.id.textViewTelaConversas);

        telaConversas.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaChat.class));
            finish();
        });

        adapter = new ConversaAdapter(this, listaRedeApoio, conversa -> {
            Intent i = new Intent(this, Chat.class);
            i.putExtra("destinatarioId", conversa.getContatoId());
            i.putExtra("nomeContato", conversa.getNomeContato());
            startActivity(i);
        });

        recycler.setAdapter(adapter);

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
            abrirHomeCorreto(); finish();
        });

        carregarRedeDeApoio();
    }

    private void carregarRedeDeApoio() {
        redeApoioIds.clear();

        db.collection("redeDeApoio")
                .whereEqualTo("criadoraId", usuarioAtualId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        try {
                            DocumentSnapshot doc = snapshot.getDocuments().get(0);
                            Object membrosObj = doc.get("membros");
                            if (membrosObj instanceof List) {
                                redeApoioIds.addAll((List<String>) membrosObj);
                            }
                        } catch (Exception e) {
                            Log.e("RedeDeApoio", "Erro ao ler membros: " + e.getMessage());
                        }
                    }
                    if(!redeApoioIds.isEmpty()) {
                        carregarConversasRedeApoio();
                    } else {
                        listaRedeApoio.clear();
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar rede", Toast.LENGTH_SHORT).show());
    }

    private void carregarConversasRedeApoio() {
        conversasListener = conversasRef
                .orderBy("dataAtualizacao", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) return;

                    if (snapshots != null) {
                        listaRedeApoio.clear();
                        Set<String> contatosProcessados = new HashSet<>();

                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            try {
                                String ultimaMensagem = doc.getString("conteudo");
                                Object horaObj = doc.get("horaEnvio");
                                String hora = "";

                                Users destinatario = doc.get("destinatario", Users.class);
                                Users remetente = doc.get("remetente", Users.class);

                                if (destinatario == null || remetente == null) continue;

                                String contatoId = null;
                                if (destinatario.getId().equals(usuarioAtualId)) {
                                    contatoId = remetente.getId();
                                } else if (remetente.getId().equals(usuarioAtualId)) {
                                    contatoId = destinatario.getId();
                                }

                                if (contatoId == null) continue;

                                if (contatosProcessados.contains(contatoId)) continue;
                                contatosProcessados.add(contatoId);

                                if (horaObj instanceof Timestamp) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                                    hora = sdf.format(((Timestamp) horaObj).toDate());
                                }

                                if (redeApoioIds.contains(contatoId)) {
                                    buscarENomearContato(contatoId, ultimaMensagem, hora);
                                }
                            } catch (Exception e) { }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void buscarENomearContato(String contatoId, String ultimaMensagem, String hora) {
        db.collection("usuaria").document(contatoId).get().addOnSuccessListener(doc -> {
            if (isDestroyed() || isFinishing()) return;
            String nomeContato;
            if (doc.exists()) {
                Boolean anonima = doc.getBoolean("anonima");
                if (Boolean.TRUE.equals(anonima)) nomeContato = doc.getString("codinome");
                else nomeContato = doc.getString("nome");
                adicionarConversa(contatoId, nomeContato, ultimaMensagem, hora);
            } else {
                db.collection("psicologo").document(contatoId).get().addOnSuccessListener(docP -> {
                    if (docP.exists()) adicionarConversa(contatoId, docP.getString("nome") + " (Psi)", ultimaMensagem, hora);
                });
            }
        });
    }
    private void adicionarConversa(String contatoId, String nomeContato, String ultimaMensagem, String hora) {
        boolean exists = false;
        for (int i = 0; i < listaRedeApoio.size(); i++) {
            if (listaRedeApoio.get(i).getContatoId().equals(contatoId)) {
                listaRedeApoio.get(i).setUltimaMensagem(ultimaMensagem);
                listaRedeApoio.get(i).setHora(hora);
                adapter.notifyItemChanged(i);
                exists = true;
                break;
            }
        }
        if (!exists) {
            listaRedeApoio.add(new Conversa(null, nomeContato, ultimaMensagem, hora, contatoId));
            adapter.notifyDataSetChanged();
        }
    }
    @Override protected void onDestroy() { super.onDestroy(); if(conversasListener != null) conversasListener.remove(); }
    public void abrirHomeCorreto(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;
        db.collection("usuaria").document(user.getUid()).get().addOnSuccessListener(doc -> {
            if(doc.exists()) startActivity(new Intent(this, TelaDeRelatos.class));
            else startActivity(new Intent(this, TelaDeRelatosPsicologo.class));
        });
    }
}