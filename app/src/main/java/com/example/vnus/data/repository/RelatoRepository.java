package com.example.vnus.data.repository;

import com.example.vnus.data.model.Relato;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

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
}
