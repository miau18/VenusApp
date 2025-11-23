package com.example.vnus.ui.auth.relato;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class RelatoAdapterPerfil extends RecyclerView.Adapter<RelatoAdapterPerfil.RelatoViewHolder> {
    private final List<Relato> relatos;
    private Context c;
    private RelatoRepository rr;
    private final boolean isPerfil;

    public RelatoAdapterPerfil(List<Relato> relatos){
        this.relatos = relatos;
        this.c = null;
        this.rr = null;
        this.isPerfil = false;
    }

    public RelatoAdapterPerfil(List<Relato> relatos, Context c, RelatoRepository rr, boolean isPerfil){
        this.relatos = relatos;
        this.c = c;
        this.rr = rr;
        this.isPerfil = isPerfil;
    }

    @NonNull
    @Override
    public RelatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if(this.c == null){
            this.c = parent.getContext();
        }
        if(this.rr == null){
            this.rr = new RelatoRepository();
        }

        View v = LayoutInflater.from(c).inflate(R.layout.card_relato_perfil, parent, false);
        return new RelatoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatoViewHolder holder, int position) {

        Relato relato = relatos.get(position);
        holder.txtCurtidas.setText(String.valueOf(relato.getCurtidas()));

        if (relato.getUsuaria() != null) {
            Usuaria usuaria = relato.getUsuaria();

            if (usuaria.getAnonima()) {
                holder.txtNomeUsuaria.setText(
                        usuaria.getCodinome() != null && !usuaria.getCodinome().isEmpty()
                                ? usuaria.getCodinome()
                                : "Anônima"
                );
            } else {
                String nome = "";
                if (usuaria.getNome() != null) nome += usuaria.getNome();
                if (usuaria.getSobrenome() != null) nome += " " + usuaria.getSobrenome();

                holder.txtNomeUsuaria.setText(!nome.trim().isEmpty() ? nome.trim() : "Usuária");
            }
        } else {
            holder.txtNomeUsuaria.setText(relato.getAnonimo() ? "Anônima" : "Usuária");
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
            Intent i = new Intent(holder.itemView.getContext(), Comentarios.class);
            i.putExtra("relatoId", relato.getId());
            holder.itemView.getContext().startActivity(i);
        });

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

        holder.iconPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PerfilAcessadoRelato.class);
            intent.putExtra("usuariaId", relato.getIdUsuaria());
            c.startActivity(intent);
        });

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu pp = new PopupMenu(c, holder.btnMenu);
            pp.getMenu().add("Editar");
            pp.getMenu().add("Excluir");

            pp.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Excluir")) {
                    rr.excluirRelato(relato.getId(), unused -> {
                        Toast.makeText(c, "Relato excluído!", Toast.LENGTH_SHORT).show();

                        relatos.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, relatos.size());
                    }, e -> {
                        Toast.makeText(c, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    return true;
                }
                else if (item.getTitle().equals("Editar")) {

                    Intent i = new Intent(c, CriacaoRelatos.class);
                    i.putExtra("relatoId", relato.getId());
                    i.putExtra("conteudoAtual", relato.getConteudo());

                    if (relato.getHospital() != null) {
                        i.putExtra("hospitalIdAtual", relato.getHospital().getId());
                    }
                    c.startActivity(i);
                    return true;
                }
                return false;
            });
            pp.show();
        });
    }

    @Override
    public int getItemCount() {
        return relatos.size();
    }

    public static class RelatoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeUsuaria, txtRelato, txtData, txtCurtidas, txtHospital;
        ImageView iconLike, iconMensagem, btnMenu, iconPerfil;

        public RelatoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeUsuaria = itemView.findViewById(R.id.txtNomeUsuaria);
            txtRelato = itemView.findViewById(R.id.txtRelato);
            txtData = itemView.findViewById(R.id.txtData);
            txtCurtidas = itemView.findViewById(R.id.txtCurtidas);
            txtHospital = itemView.findViewById(R.id.txtHospital);
            iconLike = itemView.findViewById(R.id.iconLike);
            iconMensagem = itemView.findViewById(R.id.iconMensagem);
            btnMenu = itemView.findViewById(R.id.menuOpcoes);
            iconPerfil = itemView.findViewById(R.id.iconPerfil);
        }
    }
}