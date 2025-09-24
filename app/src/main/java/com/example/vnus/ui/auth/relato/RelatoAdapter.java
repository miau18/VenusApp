package com.example.vnus.ui.auth.relato;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RelatoAdapter extends RecyclerView.Adapter<RelatoAdapter.RelatoViewHolder> {

    private List<Relato> relatos;

    public RelatoAdapter(List<Relato> relatos) {
        this.relatos = relatos;
    }

    @NonNull
    @Override
    public RelatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_relato, parent, false);
        return new RelatoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatoViewHolder holder, int position) {

        //Curtir
        Relato relato = relatos.get(position);
        holder.txtCurtidas.setText(String.valueOf(relato.getCurtidas()));

        holder.iconLike.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference relatoRef = db.collection("relato").document(relato.getId());

            relatoRef.update("curtidas", relato.getCurtidas() + 1)
                    .addOnSuccessListener(unused -> {
                        relato.setCurtidas(relato.getCurtidas() + 1);
                        holder.txtCurtidas.setText(String.valueOf(relato.getCurtidas()));
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(holder.itemView.getContext(),
                                    "Erro ao curtir: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        });



        if (relato.getAnonimo()) {
            holder.txtNomeUsuaria.setText("Anônima");
        } else if (relato.getUsuaria() != null) {
            String nome = relato.getUsuaria().getCodinome() != null ?
                    relato.getUsuaria().getCodinome() :
                    relato.getUsuaria().getNome();
            holder.txtNomeUsuaria.setText(nome);
        } else {
            holder.txtNomeUsuaria.setText("Usuária");
        }

        holder.txtRelato.setText(relato.getConteudo());

        if (relato.getDataPublicacao() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.txtData.setText(sdf.format(relato.getDataPublicacao()));
        }
    }

    @Override
    public int getItemCount() {
        return relatos.size();
    }

    public static class RelatoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeUsuaria, txtRelato, txtData, txtCurtidas;
        ImageView iconLike, iconMensagem, iconSend;

        public RelatoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeUsuaria = itemView.findViewById(R.id.txtNomeUsuaria);
            txtRelato = itemView.findViewById(R.id.txtRelato);
            txtData = itemView.findViewById(R.id.txtData);
            txtCurtidas = itemView.findViewById(R.id.txtCurtidas);
            iconLike = itemView.findViewById(R.id.iconLike);
            iconMensagem = itemView.findViewById(R.id.iconMensagem);
            iconSend = itemView.findViewById(R.id.iconSend);
        }
    }
}
