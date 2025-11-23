package com.example.vnus.ui.auth.chat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Mensagem;
import com.example.vnus.data.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private RecyclerView recyclerMensagens;
    private EditText editMensagem;
    private ImageView btnVoltar, btnEnviar, addRedeDeApoio;
    private MensagemAdapter adapter;
    private final List<Mensagem> listaMensagens = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mensagensRef;
    private ListenerRegistration chatListener;
    private String usuarioAtualId;
    private String destinatarioId, conversaId, nomeCompletoContato;
    private TextView nomeContato;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        editMensagem = findViewById(R.id.editTextMensagem);
        btnVoltar = findViewById(R.id.btnVoltar);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        btnEnviar = findViewById(R.id.btnEnviar);
        nomeContato = findViewById(R.id.textViewNomeContato);
        addRedeDeApoio = findViewById(R.id.addRedeDeApoio);

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
            Toast.makeText(this, "Erro: Não autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        destinatarioId = getIntent().getStringExtra("destinatarioId");
        nomeCompletoContato = getIntent().getStringExtra("nomeContato");

        if (destinatarioId == null || destinatarioId.isEmpty()) {
            Toast.makeText(this, "Erro: ID do destinatário não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        conversaId = gerarIdConversa(usuarioAtualId, destinatarioId);
        mensagensRef = db.collection("conversas");

        if (nomeContato != null && nomeCompletoContato != null) {
            nomeContato.setText(nomeCompletoContato);
        }

        adapter = new MensagemAdapter(listaMensagens, usuarioAtualId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setAdapter(adapter);

        carregarMensagens();

        btnEnviar.setOnClickListener(v -> {
            String texto = editMensagem.getText().toString().trim();
            if (!TextUtils.isEmpty(texto)) enviarMensagem(texto);
        });

        btnVoltar.setOnClickListener(v -> finish());

        addRedeDeApoio.setOnClickListener(v -> {
            DocumentReference redeRef = db.collection("redeDeApoio").document(usuarioAtualId);
            redeRef.get().addOnSuccessListener(doc -> {
                if (!doc.exists()) {
                    Map<String, Object> novaRede = new HashMap<>();
                    novaRede.put("criadoraId", usuarioAtualId);
                    List<String> membros = new ArrayList<>();
                    membros.add(destinatarioId);
                    novaRede.put("membros", membros);
                    redeRef.set(novaRede).addOnSuccessListener(a -> Toast.makeText(this, "Adicionado à Rede!", Toast.LENGTH_SHORT).show());
                } else {
                    List<String> membros = (List<String>) doc.get("membros");
                    if (membros == null) membros = new ArrayList<>();
                    if (!membros.contains(destinatarioId)) {
                        membros.add(destinatarioId);
                        redeRef.update("membros", membros).addOnSuccessListener(a -> Toast.makeText(this, "Adicionado à Rede!", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Usuário já está na rede.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void enviarMensagem(String texto) {
        Date dataAtual = new Date();
        Mensagem msg = new Mensagem();
        msg.setConteudo(texto);
        msg.setHoraEnvio(dataAtual);
        msg.setDataAtualizacao(dataAtual);

        Users remetente = new Users();
        remetente.setId(usuarioAtualId);
        Users destinatario = new Users();
        destinatario.setId(destinatarioId);

        msg.setRemetente(remetente);
        msg.setDestinatario(destinatario);

        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("conteudo", msg.getConteudo());
        msgMap.put("horaEnvio", msg.getHoraEnvio());
        msgMap.put("dataAtualizacao", msg.getDataAtualizacao());
        msgMap.put("remetente", msg.getRemetente());
        msgMap.put("destinatario", msg.getDestinatario());
        msgMap.put("conversaId", conversaId);

        mensagensRef.add(msgMap)
                .addOnSuccessListener(d -> editMensagem.setText(""))
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao enviar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void carregarMensagens() {
        if (conversaId == null) return;
        chatListener = mensagensRef
                .whereEqualTo("conversaId", conversaId)
                .orderBy("horaEnvio", Query.Direction.ASCENDING)
                .addSnapshotListener((value, e) -> {
                    if (e != null) return;
                    if (value != null) {
                        listaMensagens.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            listaMensagens.add(doc.toObject(Mensagem.class));
                        }
                        adapter.notifyDataSetChanged();
                        if (!listaMensagens.isEmpty())
                            recyclerMensagens.scrollToPosition(listaMensagens.size() - 1);
                    }
                });
    }

    private String gerarIdConversa(String a, String b) {
        return (a.compareTo(b) < 0) ? a + "_" + b : b + "_" + a;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatListener != null) chatListener.remove();
    }
}