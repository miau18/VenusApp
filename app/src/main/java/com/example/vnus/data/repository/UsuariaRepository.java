package com.example.vnus.data.repository;

import com.example.vnus.data.model.Usuaria;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuariaRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveUsuariaSpecifiData(Usuaria usuaria,
                                       OnSuccessListener<Void> successListener,
                                       OnFailureListener failureListener) {
        db.collection("usuaria")
                .document(usuaria.getId())
                .set(usuaria)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
