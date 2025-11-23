package com.example.vnus.ui.auth.pesquisa;

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
import com.example.vnus.ui.auth.usuaria.PerfilAcessadoRelato;

import java.util.List;

public class PesquisaAdapter extends RecyclerView.Adapter<PesquisaAdapter.UsuariaViewHolder> {

    private final Context c;
    private final List<Usuaria> listaUsuarias;

    public PesquisaAdapter(Context c, List<Usuaria> listaUsuarias) {
        this.c = c;
        this.listaUsuarias = listaUsuarias;
    }

    @NonNull
    @Override
    public UsuariaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_usuaria_chat, parent, false);
        return new UsuariaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariaViewHolder holder, int position) {
        Usuaria usuaria = listaUsuarias.get(position);
        boolean isAnonima = Boolean.TRUE.equals(usuaria.getAnonima());

        if (!isAnonima) {
            String nome = (usuaria.getNome() != null ? usuaria.getNome() : "") + " " +
                    (usuaria.getSobrenome() != null ? usuaria.getSobrenome() : "");
            holder.nomeUsuaria.setText(nome.trim());
        } else {
            holder.nomeUsuaria.setText(usuaria.getCodinome() != null ? usuaria.getCodinome() : "Anônima");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(c, PerfilAcessadoRelato.class);
            i.putExtra("usuariaId", usuaria.getId());
            c.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarias.size();
    }

    public static class UsuariaViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgPerfil;
        public TextView nomeUsuaria;

        public UsuariaViewHolder(View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            nomeUsuaria = itemView.findViewById(R.id.textViewNomeUsuario);
        }
    }
}