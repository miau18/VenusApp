package com.example.vnus.ui.auth.relato;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TelaDeRelatos extends AppCompatActivity {

    private RecyclerView rv;
    private RelatoAdapter adapter;
    private List<Relato> relatos = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_de_relato);

        rv = findViewById(R.id.posts_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RelatoAdapter(relatos);
        rv.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        carregarRelatos();

        ImageView adicionarRelato = findViewById(R.id.adicionarRelato);
        adicionarRelato.setOnClickListener(v -> {
            startActivity(new Intent(this, CriacaoRelatos.class));
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
