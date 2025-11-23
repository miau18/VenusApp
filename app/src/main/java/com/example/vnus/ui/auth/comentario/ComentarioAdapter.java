package com.example.vnus.ui.auth.comentario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Comentario;
import com.example.vnus.data.model.Usuaria;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private List<Comentario> comentarios;

    public ComentarioAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comentario, parent, false);
        return new ComentarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);

        holder.txtConteudoComentario.setText(comentario.getConteudo());

        if (comentario.getDataComentario() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.txtDataComentario.setText(sdf.format(comentario.getDataComentario()));
        }

        if (comentario.getUser() != null) {
            if (comentario.getUser() instanceof Usuaria) {
                Usuaria u = (Usuaria) comentario.getUser();
                if (u.getAnonima()) {
                    holder.txtNomeComentario.setText(u.getCodinome());
                } else {
                    holder.txtNomeComentario.setText(u.getNome());
                }
            } else {
                holder.txtNomeComentario.setText(comentario.getUser().getNome());
            }
        } else {
            holder.txtNomeComentario.setText("Usuário");
        }
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeComentario, txtConteudoComentario, txtDataComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeComentario = itemView.findViewById(R.id.txtNomeComentario);
            txtConteudoComentario = itemView.findViewById(R.id.txtConteudoComentario);
            txtDataComentario = itemView.findViewById(R.id.txtDataComentario);
        }
    }
}
