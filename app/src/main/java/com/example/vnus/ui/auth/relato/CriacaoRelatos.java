package com.example.vnus.ui.auth.relato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vnus.R;
import com.example.vnus.data.model.Hospital;
import com.example.vnus.data.model.Relato;
import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.RelatoRepository;
import com.example.vnus.ui.auth.chat.TelaChat;
import com.example.vnus.ui.auth.informacoes.TelaInformacoes;
import com.example.vnus.ui.auth.notificacao.APIService;
import com.example.vnus.ui.auth.notificacao.OneSignalNotification;
import com.example.vnus.ui.auth.pesquisa.TelaPesquisa;
import com.example.vnus.ui.auth.usuaria.PerfilUsuaria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CriacaoRelatos extends AppCompatActivity {

    private EditText criarRelato;
    private Button btnPublicar;
    private RelatoRepository rr;
    private ImageView home, informacoes, perfilUsuaria, chat, lupa;
    private Spinner spinnerHospital;
    private List<Hospital> listaHospitais = new ArrayList<>();
    private boolean isEditMode = false;
    private String relatoIdEditar = null;
    private String hospitalIdEditar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criacao_relato);

        perfilUsuaria = findViewById(R.id.config_icon);
        perfilUsuaria.setOnClickListener(v -> {
            startActivity(new Intent(CriacaoRelatos.this, PerfilUsuaria.class));
            finish();
        });

        lupa = findViewById(R.id.lupa);
        lupa.setOnClickListener(v -> {
            startActivity(new Intent(this, TelaPesquisa.class));
            finish();
        });

        informacoes = findViewById(R.id.informacoes);
        informacoes.setOnClickListener(v -> {
            startActivity(new Intent(CriacaoRelatos.this, TelaInformacoes.class));
            finish();
        });

        home = findViewById(R.id.iconHome);
        home.setOnClickListener(v -> {
            startActivity(new Intent(CriacaoRelatos.this, TelaDeRelatos.class));
            finish();
        });

        chat = findViewById(R.id.iconChat);
        chat.setOnClickListener(v -> {
            startActivity(new Intent(CriacaoRelatos.this, TelaChat.class));
            finish();
        });

        criarRelato = findViewById(R.id.editTextCriarRelato);
        btnPublicar = findViewById(R.id.btnPublicar);
        spinnerHospital = findViewById(R.id.spinnerHospital);
        rr = new RelatoRepository();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("relatoId")) {
            isEditMode = true;
            relatoIdEditar = extras.getString("relatoId");
            criarRelato.setText(extras.getString("conteudoAtual"));
            hospitalIdEditar = extras.getString("hospitalIdAtual");
            btnPublicar.setText("Salvar Alterações");
        }

        FirebaseFirestore.getInstance().collection("hospital").get().addOnSuccessListener(snapshot -> {
            listaHospitais.clear();
            listaHospitais.add(new Hospital(null, "Selecione um hospital caso deseje", null, null));
            int pos = 0;
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Hospital h = doc.toObject(Hospital.class);
                if (h != null) {
                    h.setId(doc.getId());
                    listaHospitais.add(h);
                    if (isEditMode && hospitalIdEditar != null && h.getId().equals(hospitalIdEditar)) pos = listaHospitais.size() - 1;
                }
            }
            ArrayAdapter<Hospital> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, listaHospitais);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerHospital.setAdapter(adapter);
            spinnerHospital.setSelection(pos);
        });

        btnPublicar.setOnClickListener(v -> {
            String conteudo = criarRelato.getText().toString().trim();
            if (conteudo.isEmpty()) return;
            int pos = spinnerHospital.getSelectedItemPosition();
            Hospital hosp = (pos > 0 && pos < listaHospitais.size()) ? listaHospitais.get(pos) : null;

            if (isEditMode) {
                rr.editarRelato(relatoIdEditar, conteudo, hosp,
                        unused -> {
                            Toast.makeText(CriacaoRelatos.this, "Atualizado!", Toast.LENGTH_SHORT).show();
                            navegarParaHome();
                        },
                        e -> Toast.makeText(CriacaoRelatos.this, "Erro ao atualizar", Toast.LENGTH_SHORT).show());
            } else {
                String id = UUID.randomUUID().toString();
                Relato relato = new Relato(id, conteudo, false, new Date(), 0);
                relato.setHospital(hosp);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    if (user.isAnonymous()) {
                        SharedPreferences prefs = getSharedPreferences("DadosUsuario", MODE_PRIVATE);
                        String codinomeSalvo = prefs.getString("codinome_real", "Anônima");
                        String idBancoSalvo = prefs.getString("id_banco_real", null);

                        if(idBancoSalvo == null) {
                            Toast.makeText(CriacaoRelatos.this, "Erro: Relogue para publicar.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Usuaria anonima = new Usuaria();
                        anonima.setId(idBancoSalvo);
                        anonima.setAnonima(true);
                        anonima.setCodinome(codinomeSalvo);

                        relato.setUsuaria(anonima);
                        relato.setIdUsuaria(idBancoSalvo);
                        relato.setAnonimo(true);

                        salvar(relato);
                    } else {
                        FirebaseFirestore.getInstance().collection("usuaria").document(user.getUid()).get()
                                .addOnSuccessListener(snapshot -> {
                                    Usuaria u = snapshot.exists() ? snapshot.toObject(Usuaria.class) : new Usuaria();
                                    if(!snapshot.exists()) { u.setId(user.getUid()); u.setNome("Usuária"); }

                                    relato.setUsuaria(u);
                                    relato.setIdUsuaria(user.getUid());
                                    relato.setAnonimo(false);
                                    salvar(relato);
                                });
                    }
                }
            }
        });
    }

    private void salvar(Relato relato) {
        rr.saveRelato(relato,
                unused -> {
                    notificarSeguidores(relato.getIdUsuaria());
                    Toast.makeText(CriacaoRelatos.this, "Publicado!", Toast.LENGTH_SHORT).show();
                    navegarParaHome();
                },
                e -> Toast.makeText(CriacaoRelatos.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navegarParaHome() {
        Intent intent = new Intent(CriacaoRelatos.this, TelaDeRelatos.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void notificarSeguidores(String meuId) {
        String ONESIGNAL_APP_ID = "d23bd444-9556-43a1-bb37-9178eb340f08";

        Map<String, String> contents = new HashMap<>();
        contents.put("en", "Nova publicação de alguém que você segue!");
        contents.put("pt", "Nova publicação de alguém que você segue!");

        Map<String, String> headings = new HashMap<>();
        headings.put("en", "Novo Relato");
        headings.put("pt", "Novo Relato");

        List<Map<String, Object>> filters = new ArrayList<>();
        Map<String, Object> filter = new HashMap<>();
        filter.put("field", "tag");
        filter.put("key", "seguindo_" + meuId);
        filter.put("relation", "=");
        filter.put("value", "1");
        filters.add(filter);

        OneSignalNotification notification = new OneSignalNotification(ONESIGNAL_APP_ID, contents, headings, filters);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://onesignal.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(APIService.class).sendNotification(notification).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {}
            @Override
            public void onFailure(Call<Object> call, Throwable t) {}
        });
    }
}