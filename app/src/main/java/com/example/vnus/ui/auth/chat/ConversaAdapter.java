package com.example.vnus.ui.auth.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Conversa;

import java.util.List;

public class ConversaAdapter extends RecyclerView.Adapter<ConversaAdapter.ConversaViewHolder> {

    private final Context context;
    private final List<Conversa> conversas;
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Conversa conversa);
    }

    public ConversaAdapter(Context context, List<Conversa> conversas, OnItemClickListener listener) {
        this.context = context;
        this.conversas = conversas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversa, parent, false);
        return new ConversaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversaViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);

        holder.nome.setText(conversa.getNomeContato());
        holder.ultimaMsg.setText(conversa.getUltimaMensagem());
        holder.hora.setText(conversa.getHora());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(conversa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public static class ConversaViewHolder extends RecyclerView.ViewHolder {
        TextView nome, ultimaMsg, hora;

        public ConversaViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textViewNomeContato);
            ultimaMsg = itemView.findViewById(R.id.textViewMensagem);
            hora = itemView.findViewById(R.id.textViewHora);
        }
    }
}