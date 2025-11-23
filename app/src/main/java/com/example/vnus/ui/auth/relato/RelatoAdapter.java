package com.example.vnus.ui.auth.relato;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.RelatoRepository;
import com.example.vnus.ui.auth.comentario.Comentarios;
import com.example.vnus.ui.auth.usuaria.PerfilAcessadoRelato;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RelatoAdapter extends RecyclerView.Adapter<RelatoAdapter.RelatoViewHolder> {

    private final List<Relato> relatos;
    private Context c;
    private RelatoRepository rr;

    public RelatoAdapter(List<Relato> relatos) {
        this.relatos = relatos;
        this.c = null;
        this.rr = null;
    }

    public RelatoAdapter(List<Relato> relatos, Context c, RelatoRepository rr) {
        this.relatos = relatos;
        this.c = c;
        this.rr = rr;
    }

    @NonNull
    @Override
    public RelatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (this.c == null) this.c = parent.getContext();
        if (this.rr == null) this.rr = new RelatoRepository();
        View v = LayoutInflater.from(c).inflate(R.layout.card_relato, parent, false);
        return new RelatoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatoViewHolder holder, int position) {
        Relato relato = relatos.get(position);

        holder.txtCurtidas.setText(String.valueOf(relato.getCurtidas()));

        if (relato.getUsuaria() != null) {
            Usuaria usuaria = relato.getUsuaria();
            if (Boolean.TRUE.equals(usuaria.getAnonima())) {
                holder.txtNomeUsuaria.setText(usuaria.getCodinome() != null ? usuaria.getCodinome() : "Anônima");
            } else {
                String nome = (usuaria.getNome() != null ? usuaria.getNome() : "") + " " +
                        (usuaria.getSobrenome() != null ? usuaria.getSobrenome() : "");
                holder.txtNomeUsuaria.setText(nome.trim().isEmpty() ? "Usuária" : nome.trim());
            }
        } else {
            holder.txtNomeUsuaria.setText("Anônima");
        }

        holder.txtRelato.setText(relato.getConteudo());
        if (relato.getDataPublicacao() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.txtData.setText(sdf.format(relato.getDataPublicacao()));
        } else {
            holder.txtData.setText("");
        }

        if (relato.getHospital() != null && relato.getHospital().getNome() != null) {
            holder.txtHospital.setText("Hospital: " + relato.getHospital().getNome());
            holder.txtHospital.setVisibility(View.VISIBLE);
        } else {
            holder.txtHospital.setVisibility(View.GONE);
        }

        holder.iconMensagem.setOnClickListener(v -> {
            Intent i = new Intent(c, Comentarios.class);
            i.putExtra("relatoId", relato.getId());
            c.startActivity(i);
        });

        holder.iconLike.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference relatoRef = db.collection("relato").document(relato.getId());
            relatoRef.update("curtidas", relato.getCurtidas() + 1)
                    .addOnSuccessListener(unused -> {
                        relato.setCurtidas(relato.getCurtidas() + 1);
                        holder.txtCurtidas.setText(String.valueOf(relato.getCurtidas()));
                    });
        });

        holder.iconPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(c, PerfilAcessadoRelato.class);
            intent.putExtra("usuariaId", relato.getIdUsuaria());
            c.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return relatos.size();
    }

    public static class RelatoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeUsuaria, txtRelato, txtData, txtCurtidas, txtHospital;
        ImageView iconLike, iconMensagem, iconPerfil;

        public RelatoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeUsuaria = itemView.findViewById(R.id.txtNomeUsuaria);
            txtRelato = itemView.findViewById(R.id.txtRelato);
            txtData = itemView.findViewById(R.id.txtData);
            txtCurtidas = itemView.findViewById(R.id.txtCurtidas);
            txtHospital = itemView.findViewById(R.id.txtHospital);
            iconLike = itemView.findViewById(R.id.iconLike);
            iconMensagem = itemView.findViewById(R.id.iconMensagem);
            iconPerfil = itemView.findViewById(R.id.iconPerfil);
        }
    }
}