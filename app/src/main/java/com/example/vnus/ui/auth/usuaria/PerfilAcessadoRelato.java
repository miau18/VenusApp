package com.example.vnus.ui.auth.usuaria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vnus.R;
import com.example.vnus.data.model.Relato;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.RelatoRepository;
import com.example.vnus.ui.auth.chat.Chat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.relato.RelatoAdapter;
import com.example.vnus.ui.auth.relato.TelaDeRelatos;
import com.example.vnus.ui.auth.relato.TelaDeRelatosPsicologo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

public class PerfilAcessadoRelato extends AppCompatActivity {

    private TextView txtNome, txtEmail, txtLabelRelatos;
    private ImageView home, informacoes, lupa;
    private Button btnEnviarMensagem;
    private SwitchCompat switchNotificacoes;
    private RecyclerView recyclerRelatos;
    private FirebaseFirestore db;
    private String usuarioVisitadoId;
    private RelatoAdapter adapter;
    private List<Relato> listaRelatos = new ArrayList<>();
    private RelatoRepository rr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_acessado_usuaria);

        db = FirebaseFirestore.getInstance();
        rr = new RelatoRepository();

        usuarioVisitadoId = getIntent().getStringExtra("usuariaId");

        txtNome = findViewById(R.id.textViewNome2);
        txtEmail = findViewById(R.id.textViewEmail2);
        btnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);
        switchNotificacoes = findViewById(R.id.switchNotificacoes);
        recyclerRelatos = findViewById(R.id.relatos);
        txtLabelRelatos = findViewById(R.id.textViewRelatos);

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            abrirHomeCorreto();
            finish();
        });

        lupa = findViewById(R.id.lupa);
        lupa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
            finish();
        });

        informacoes = findViewById(R.id.informacoes);
        informacoes.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaInformacoes.class));
            finish();
        });

        recyclerRelatos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RelatoAdapter(listaRelatos, this, rr);
        recyclerRelatos.setAdapter(adapter);

        if (usuarioVisitadoId != null) {
            carregarDadosUsuaria();
            carregarRelatos();
            configurarSwitchNotificacoes();
        } else {
            Toast.makeText(this, "ID de usuário inválido", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnEnviarMensagem.setOnClickListener(v -> {
            Intent i = new Intent(this, Chat.class);
            i.putExtra("destinatarioId", usuarioVisitadoId);
            i.putExtra("nomeContato", txtNome.getText().toString());
            startActivity(i);
        });
    }

    private void configurarSwitchNotificacoes() {
        if (OneSignal.getUser().getTags() != null && OneSignal.getUser().getTags().containsKey("seguindo_" + usuarioVisitadoId)) {
            switchNotificacoes.setChecked(true);
        } else {
            switchNotificacoes.setChecked(false);
        }

        switchNotificacoes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                OneSignal.getUser().addTag("seguindo_" + usuarioVisitadoId, "1");
                Toast.makeText(this, "Notificações ativadas!", Toast.LENGTH_SHORT).show();
            } else {
                OneSignal.getUser().removeTag("seguindo_" + usuarioVisitadoId);
                Toast.makeText(this, "Notificações desativadas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDadosUsuaria() {
        db.collection("usuaria").document(usuarioVisitadoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Usuaria u = doc.toObject(Usuaria.class);
                        if (u != null) {
                            if (Boolean.TRUE.equals(u.getAnonima())) {
                                txtNome.setText(u.getCodinome() != null ? u.getCodinome() : "Anônima");
                                txtEmail.setText("Perfil Anônimo");
                            } else {
                                String nome = (u.getNome() != null ? u.getNome() : "") + " " +
                                        (u.getSobrenome() != null ? u.getSobrenome() : "");
                                txtNome.setText(nome.trim());
                                txtEmail.setText(u.getEmail());
                            }
                        }
                    } else {
                        db.collection("psicologo").document(usuarioVisitadoId).get()
                                .addOnSuccessListener(docPsi -> {
                                    if(docPsi.exists()) {
                                        txtNome.setText(docPsi.getString("nome") + " (Psi)");
                                        txtEmail.setText(docPsi.getString("email"));
                                    }
                                });
                    }
                });
    }

    private void carregarRelatos() {
        rr.getRelatoPorUsuaria(usuarioVisitadoId, relatos -> {
            listaRelatos.clear();
            listaRelatos.addAll(relatos);
            adapter.notifyDataSetChanged();

            if (listaRelatos.isEmpty()) txtLabelRelatos.setText("Nenhum relato encontrado.");
            else txtLabelRelatos.setText("Relatos (" + relatos.size() + ")");

        }, e -> {});
    }

    public void abrirHomeCorreto(){
        SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
        String tipoUsuario = prefs.getString("tipo_usuario", null);

        if ("usuaria".equals(tipoUsuario)) {
            Intent intent = new Intent(this, TelaDeRelatos.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
            return;
        } else if ("psicologo".equals(tipoUsuario)) {
            Intent intent = new Intent(this, TelaDeRelatosPsicologo.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuaria").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if(doc.exists()){
                        prefs.edit().putString("tipo_usuario", "usuaria").apply();
                        Intent intent = new Intent(this, TelaDeRelatos.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        db.collection("psicologo").document(uid).get()
                                .addOnSuccessListener(docPsi -> {
                                    if(docPsi.exists()){
                                        prefs.edit().putString("tipo_usuario", "psicologo").apply();
                                        Intent intent = new Intent(this, TelaDeRelatosPsicologo.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    } else {
                                        Toast.makeText(this, "Perfil não encontrado", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao verificar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}