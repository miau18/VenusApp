package com.example.vnus.data.repository;

import com.example.vnus.data.model.Hospital;
import com.example.vnus.data.model.Relato;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RelatoRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveRelato(Relato relato,
                           OnSuccessListener<Void> successListener,
                           OnFailureListener failureListener){

        if(relato.getDataPublicacao() == null){
            relato.setDataPublicacao(new Date());
        }
        db.collection("relato")
                .document(relato.getId())
                .set(relato)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getRelatoPorUsuaria(String usuariaId,
                                    OnSuccessListener <java.util.List<Relato>> successListener,
                                    OnFailureListener failureListener){
        db.collection("relato")
                .whereEqualTo("idUsuaria", usuariaId)
                .get()
                .addOnSuccessListener(query -> {
                    java.util.List<Relato> lista = new java.util.ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : query){
                        Relato r = doc.toObject(Relato.class);
                        r.setId(doc.getId());
                        lista.add(r);
                    }
                    successListener.onSuccess(lista);
                })
                .addOnFailureListener(failureListener);
    }

    public void editarRelato(String relatoId, String novoConteudo, Hospital hospital,
                             OnSuccessListener<Void> successListener,
                             OnFailureListener failureListener){
        Map<String, Object> atualizar = new HashMap<>();
        atualizar.put("conteudo", novoConteudo);
        atualizar.put("hospital", hospital);

        db.collection("relato")
                .document(relatoId)
                .update(atualizar)
                .addOnFailureListener(failureListener)
                .addOnSuccessListener(successListener);
    }

    public void excluirRelato(String relatoId,
                             OnSuccessListener<Void> successListener,
                             OnFailureListener failureListener){
        db.collection("relato")
                .document(relatoId)
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
