package com.example.vnus.ui.auth.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Conversa;
import com.example.vnus.data.model.Users;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TelaChat extends AppCompatActivity {

    private EditText campoPesquisa;
    private RecyclerView recycler;
    private ConversaAdapter conversaAdapter;
    private UserPesquisaChat userPesquisaAdapter;
    private final List<Conversa> listaConversas = new ArrayList<>();
    private final List<Usuaria> listaPesquisa = new ArrayList<>();
    private String usuarioAtualId;
    private ListenerRegistration conversasListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference conversasRef = db.collection("conversas");
    private TextView redeDeApoio;
    private ImageView home, lupa, informacoes;
    private boolean isPsicologo = false;
    private final Set<String> uidsEncontrados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_chat);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.isAnonymous()) {
                SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
                usuarioAtualId = prefs.getString("id_banco_real", null);
                if (usuarioAtualId == null) usuarioAtualId = user.getUid();
            } else {
                usuarioAtualId = user.getUid();
            }
        } else {
            finish();
            return;
        }

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
            startActivity(new Intent(this, TelaDeRelatos.class));
            finish();
        });
        redeDeApoio = findViewById(R.id.textViewRedeApoio);
        redeDeApoio.setOnClickListener(v -> {
            startActivity(new Intent(this, RedeDeApoio.class));
            finish();
        });

        campoPesquisa = findViewById(R.id.editTextPesquisa);
        recycler = findViewById(R.id.recyclerConversas);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        db.collection("psicologo").document(usuarioAtualId).get().addOnSuccessListener(doc -> {
            if(doc.exists()) isPsicologo = true;
        });

        conversaAdapter = new ConversaAdapter(this, listaConversas, conversa -> {
            Intent i = new Intent(TelaChat.this, Chat.class);
            i.putExtra("destinatarioId", conversa.getContatoId());
            i.putExtra("nomeContato", conversa.getNomeContato());
            startActivity(i);
        });

        userPesquisaAdapter = new UserPesquisaChat(this, listaPesquisa);

        recycler.setAdapter(conversaAdapter);

        carregarConversasExistentes();

        campoPesquisa.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                pesquisarUsuariosFirestore(s.toString());
            }
        });
    }

    private void carregarConversasExistentes() {
        conversasListener = conversasRef
                .orderBy("horaEnvio", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) return;

                    if (snapshots != null) {
                        listaConversas.clear();
                        Set<String> contatosProcessados = new HashSet<>();

                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String ultimaMensagem = doc.getString("conteudo");
                            Object horaObj = doc.get("horaEnvio");
                            String hora = "";
                            String contatoId;

                            Users destinatario = doc.get("destinatario", Users.class);
                            Users remetente = doc.get("remetente", Users.class);

                            if (destinatario == null || remetente == null) continue;

                            if (destinatario.getId().equals(usuarioAtualId)) {
                                contatoId = remetente.getId();
                            } else if (remetente.getId().equals(usuarioAtualId)) {
                                contatoId = destinatario.getId();
                            } else {
                                continue;
                            }

                            if (contatosProcessados.contains(contatoId)) continue;
                            contatosProcessados.add(contatoId);

                            if (horaObj instanceof Timestamp) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                                hora = sdf.format(((Timestamp) horaObj).toDate());
                            } else if (horaObj instanceof String) {
                                hora = (String) horaObj;
                            }

                            Conversa novaConversa = new Conversa(null, "Carregando...", ultimaMensagem, hora, contatoId);
                            listaConversas.add(novaConversa);

                            atualizarNomeDoContato(novaConversa, contatoId);
                        }

                        if (campoPesquisa.getText().toString().isEmpty()) {
                            conversaAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void atualizarNomeDoContato(Conversa conversaParaAtualizar, String contatoId) {
        db.collection("usuaria").document(contatoId).get()
                .addOnSuccessListener(doc -> {
                    String nomeFinal;
                    if (doc.exists()) {
                        Boolean anonima = doc.getBoolean("anonima");
                        String codinome = doc.getString("codinome");
                        if (Boolean.TRUE.equals(anonima)) {
                            nomeFinal = (codinome != null) ? codinome : "Anônima";
                        } else {
                            nomeFinal = (doc.getString("nome") != null ? doc.getString("nome") : "") + " " + (doc.getString("sobrenome") != null ? doc.getString("sobrenome") : "");
                        }
                    } else {
                        nomeFinal = "Verificando...";
                        db.collection("psicologo").document(contatoId).get()
                                .addOnSuccessListener(docP -> {
                                    String nomePsi;
                                    if (docP.exists()) {
                                        nomePsi = docP.getString("nome") + " " + docP.getString("sobrenome") + " (Psicóloga)";
                                    } else {
                                        nomePsi = "Usuário Excluído";
                                    }
                                    conversaParaAtualizar.setNomeContato(nomePsi);
                                    if (campoPesquisa.getText().toString().isEmpty()) {
                                        conversaAdapter.notifyDataSetChanged();
                                    }
                                });
                    }

                    if (!nomeFinal.equals("Verificando...")) {
                        conversaParaAtualizar.setNomeContato(nomeFinal);
                        if (campoPesquisa.getText().toString().isEmpty()) {
                            conversaAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    private void pesquisarUsuariosFirestore(String texto) {
        listaPesquisa.clear();
        uidsEncontrados.clear();

        if (texto == null || texto.trim().isEmpty()) {
            recycler.setAdapter(conversaAdapter);
            conversaAdapter.notifyDataSetChanged();
            return;
        }

        if(recycler.getAdapter() != userPesquisaAdapter) {
            recycler.setAdapter(userPesquisaAdapter);
        }
        userPesquisaAdapter.notifyDataSetChanged();

        final String termo = texto.toLowerCase().trim();

        db.collection("usuaria").get().addOnSuccessListener(query -> {
            String textoAtual = campoPesquisa.getText().toString().toLowerCase().trim();
            if (!textoAtual.equals(termo)) return;

            for (QueryDocumentSnapshot doc : query) {
                if (doc.getId().equals(usuarioAtualId)) continue;

                Usuaria u = doc.toObject(Usuaria.class);
                u.setId(doc.getId());

                Boolean anonima = u.getAnonima();
                String nomeBusca = "";
                if(Boolean.TRUE.equals(anonima)) {
                    nomeBusca = u.getCodinome();
                } else {
                    nomeBusca = (u.getNome() + " " + u.getSobrenome());
                }

                if (nomeBusca != null && nomeBusca.toLowerCase().contains(termo)) {
                    if (uidsEncontrados.add(doc.getId())) {
                        listaPesquisa.add(u);
                    }
                }
            }
            userPesquisaAdapter.notifyDataSetChanged();
        });

        if (!isPsicologo) {
            db.collection("psicologo").get().addOnSuccessListener(query -> {
                String textoAtual = campoPesquisa.getText().toString().toLowerCase().trim();
                if (!textoAtual.equals(termo)) return;

                for (QueryDocumentSnapshot doc : query) {
                    if (doc.getId().equals(usuarioAtualId)) continue;

                    String nomeCompleto = (doc.getString("nome") + " " + doc.getString("sobrenome")).toLowerCase();

                    if (nomeCompleto.contains(termo)) {
                        if (uidsEncontrados.add(doc.getId())) {
                            Usuaria psi = new Usuaria();
                            psi.setId(doc.getId());
                            psi.setNome(doc.getString("nome"));
                            psi.setSobrenome(doc.getString("sobrenome") + " (Psicóloga)");
                            psi.setAnonima(false);
                            listaPesquisa.add(psi);
                        }
                    }
                }
                userPesquisaAdapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conversasListener != null) conversasListener.remove();
    }
}