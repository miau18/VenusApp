package com.example.vnus.ui.auth.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Mensagem;

import java.util.List;

public class MensagemAdapter extends RecyclerView.Adapter<MensagemAdapter.MensagemViewHolder> {

    private final List<Mensagem> listaMensagens;
    private final String idUsuarioLogado;
    public static final int TIPO_REMETENTE = 0;
    public static final int TIPO_DESTINATARIO = 1;

    public MensagemAdapter(List<Mensagem> listaMensagens, String idUsuarioLogado) {
        this.listaMensagens = listaMensagens;
        this.idUsuarioLogado = idUsuarioLogado;
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = listaMensagens.get(position);
        if (mensagem.getRemetente() != null &&
                mensagem.getRemetente().getId().equals(idUsuarioLogado)) {
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;
    }

    @NonNull
    @Override
    public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;
        if (viewType == TIPO_REMETENTE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.mensagem_enviada, parent, false);
        } else {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.mensagem_recebida, parent, false);
        }
        return new MensagemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagemViewHolder holder, int position) {
        Mensagem mensagem = listaMensagens.get(position);

        if (holder.msg != null) {
            holder.msg.setText(mensagem.getConteudo());
        }
    }

    @Override
    public int getItemCount() {
        return listaMensagens.size();
    }

    public static class MensagemViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        public MensagemViewHolder(@NonNull View itemView) {
            super(itemView);

            msg = itemView.findViewById(R.id.textViewMensagemEnviada);

            if (msg == null) {
                msg = itemView.findViewById(R.id.textViewMensagemRecebida);
            }
        }
    }
}