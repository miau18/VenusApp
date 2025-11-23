package com.example.vnus.data.repository;

import com.example.vnus.data.model.Comentario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ComentarioRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveComentarioSpecificData(String relatoId,
                                           Comentario comentario,
                                           OnSuccessListener<Void> successListener,
                                           OnFailureListener failureListener){

        db.collection("relato")
                .document(relatoId)
                .collection("comentario")
                .add(comentario)
                .addOnSuccessListener(docRef -> successListener.onSuccess(null))
                .addOnFailureListener(failureListener);
    }

    public ListenerRegistration listenComentarios(String relatoId,
                                                  com.google.firebase.firestore.EventListener<QuerySnapshot> listener) {
        return db.collection("relato")
                .document(relatoId)
                .collection("comentario")
                .orderBy("dataComentario", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }
}
