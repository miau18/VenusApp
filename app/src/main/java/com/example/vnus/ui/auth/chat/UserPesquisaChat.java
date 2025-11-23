package com.example.vnus.ui.auth.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Usuaria;

import java.util.List;

public class UserPesquisaChat extends RecyclerView.Adapter<UserPesquisaChat.ViewHolder> {

    private final Context context;
    private final List<Usuaria> listaUsuarios;

    public UserPesquisaChat(Context context, List<Usuaria> listaUsuarios) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_usuaria_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuaria usuario = listaUsuarios.get(position);

        String nomeExibicao;
        if (Boolean.TRUE.equals(usuario.getAnonima())) {
            nomeExibicao = usuario.getCodinome() != null ? usuario.getCodinome() : "Anônima";
        } else {
            nomeExibicao = (usuario.getNome() != null ? usuario.getNome() : "") + " " +
                    (usuario.getSobrenome() != null ? usuario.getSobrenome() : "");
        }

        holder.txtNome.setText(nomeExibicao.trim());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("destinatarioId", usuario.getId());
            intent.putExtra("nomeContato", nomeExibicao.trim());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        ImageView imgPerfil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.textViewNomeUsuario);
            imgPerfil = itemView.findViewById(R.id.imgPerfil);
        }
    }
}