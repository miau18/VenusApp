package com.example.vnus.ui.auth.relato;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.psicologo.PerfilPsicologo;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TelaDeRelatosPsicologo extends AppCompatActivity {
    private RecyclerView rv;
    private RelatoAdapter adapter;
    private List<Relato> relatos = new ArrayList<>();
    private FirebaseFirestore db;
    private ImageView perfilPsicologo, informacoes, chat, lupa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_relato_psicologo);

        rv = findViewById(R.id.posts_recycler_view_2);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RelatoAdapter(relatos);
        rv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        carregarRelatos();

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

        perfilPsicologo = findViewById(R.id.config_icon);
        perfilPsicologo.setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilPsicologo.class));
            finish();
        });

        lupa = findViewById(R.id.lupa);
        lupa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
            finish();
        });
    }

    private void carregarRelatos() {
        db.collection("relato")
                .orderBy("dataPublicacao", Query.Direction.DESCENDING)
                .addSnapshotListener((@Nullable QuerySnapshot snapshots,
                                      @Nullable FirebaseFirestoreException e) -> {
                    if (e != null || snapshots == null) return;

                    relatos.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Relato relato = doc.toObject(Relato.class);
                        relatos.add(relato);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
