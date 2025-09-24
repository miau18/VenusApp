package com.example.vnus.data.repository;

import com.example.vnus.data.model.Psicologo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class PsicologoRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void savePsicologoSpecificData(Psicologo psicologo,
                                          OnSuccessListener<Void> successListener,
                                          OnFailureListener failureListener) {

        db.collection("psicologo")
                .document(psicologo.getId())
                .set(psicologo)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

    }
}
