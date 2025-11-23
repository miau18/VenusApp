package com.example.vnus.ui.auth.informacoes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import java.util.List;

public class InformacoesAdapter extends RecyclerView.Adapter<InformacoesAdapter.ViewHolder> {

    private List<TemaInformacoes> listaTemas;
    private Context context;

    public InformacoesAdapter(List<TemaInformacoes> listaTemas, Context context) {
        this.listaTemas = listaTemas;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_informacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TemaInformacoes tema = listaTemas.get(position);

        holder.txtTitulo.setText(tema.getTitulo());
        holder.txtDescricao.setText(tema.getDescricao());
        holder.imgIcone.setImageResource(tema.getImagem());

        holder.layoutItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, TelaPostagem.class);
            intent.putExtra("posicao_atual", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaTemas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDescricao;
        ImageView imgIcone;
        LinearLayout layoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTituloTema);
            txtDescricao = itemView.findViewById(R.id.txtDescricaoTema);
            imgIcone = itemView.findViewById(R.id.imgTema);
            layoutItem = itemView.findViewById(R.id.layoutItemClick);
        }
    }
}