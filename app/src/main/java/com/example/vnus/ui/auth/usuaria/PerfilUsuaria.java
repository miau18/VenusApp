package com.example.vnus.ui.auth.usuaria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.RelatoRepository;
import com.example.vnus.data.repository.UsuariaRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.relato.RelatoAdapterPerfil;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PerfilUsuaria extends AppCompatActivity {

    private TextView textNome, textEmail;
    private Button btnEditar, btnSair;
    private RecyclerView relatos;
    private UsuariaRepository ur;
    private RelatoRepository rr;
    private Usuaria usuariaAtual;
    private List<Relato> listaRelatos = new ArrayList<>();
    private RelatoAdapterPerfil adapter;
    private boolean anonima;
    private ImageView home, informacoes, chat, lupa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuaria);

        textNome = findViewById(R.id.textViewNome);
        textEmail = findViewById(R.id.textViewEmail);
        btnEditar = findViewById(R.id.btnEditarPerfil);
        btnSair = findViewById(R.id.btnSair);
        relatos = findViewById(R.id.relatos);

        ur = new UsuariaRepository();
        rr = new RelatoRepository();

        relatos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RelatoAdapterPerfil(listaRelatos, this, rr, true);
        relatos.setAdapter(adapter);

        chat = findViewById(R.id.iconChat);
        chat.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaChat.class));
            finish();
        });

        informacoes = findViewById(R.id.informacoes);
        informacoes.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaInformacoes.class));
            finish();
        });

        lupa = findViewById(R.id.lupa);
        lupa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
            finish();
        });

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaDeRelatos.class));
            finish();
        });

        btnEditar.setOnClickListener(v -> {
            if (usuariaAtual != null) {
                Intent i = new Intent(this, EditarContaUsuaria.class);
                i.putExtra("usuariaId", usuariaAtual.getId());
                i.putExtra("anonima", anonima);
                startActivity(i);
            }
        });

        btnSair.setOnClickListener(v -> {
            getSharedPreferences("DadosUsuario", MODE_PRIVATE).edit().clear().commit();
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(this, "Logout realizado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginUsuaria.class));
            finish();
        });

        carregarDadosUsuaria();
    }

    private void carregarDadosUsuaria() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginUsuaria.class));
            finish();
            return;
        }

        if (user.isAnonymous()) {
            SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
            String codinome = prefs.getString("codinome_real", "Anônima");
            String idBanco = prefs.getString("id_banco_real", null);

            if (idBanco == null) {
                btnSair.performClick();
                return;
            }

            textNome.setText(codinome);
            textEmail.setText("Perfil Anônimo");
            anonima = true;

            usuariaAtual = new Usuaria();
            usuariaAtual.setId(idBanco);
            usuariaAtual.setCodinome(codinome);
            usuariaAtual.setAnonima(true);

            carregarRelatos(idBanco);
            return;
        }

        String idUsuaria = user.getUid();
        ur.getUsuariaPorId(idUsuaria, usuaria -> {
            usuariaAtual = usuaria;
            anonima = usuaria.getAnonima();
            if (anonima) {
                textNome.setText(usuaria.getCodinome());
                textEmail.setText("Usuária anônima");
            } else {
                textNome.setText(usuaria.getNome() + " " + usuaria.getSobrenome());
                textEmail.setText(usuaria.getEmail());
            }
            carregarRelatos(idUsuaria);
        }, e -> Toast.makeText(this, "Erro ao carregar", Toast.LENGTH_SHORT).show());
    }

    private void carregarRelatos(String idUsuaria) {
        rr.getRelatoPorUsuaria(idUsuaria, relatoes -> {
            listaRelatos.clear();
            listaRelatos.addAll(relatoes);
            adapter.notifyDataSetChanged();
        }, e -> {});
    }
}