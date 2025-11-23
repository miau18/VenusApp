package com.example.vnus.data.repository;

import android.content.Context;

import com.example.vnus.data.model.Psicologo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class PsicologoRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void savePsicologoSpecificData(Psicologo psicologo,
                                          OnSuccessListener<Void> successListener,
                                          OnFailureListener failureListener) {

        db.collection("psicologo")
                .document(psicologo.getId())
                .set(psicologo)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

    }

    public void logoutPsicologo(Context c) {

        FirebaseAuth.getInstance().signOut();
    }

    public String getPsicologoLogadoId(Context c) {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        } else {
            return "Nenhum usuário";
        }
    }

    public void getPsicologoPorId(String psicologoId,
                                  OnSuccessListener<Psicologo> successListener,
                                  OnFailureListener failureListener) {
        db.collection("psicologo")
                .document(psicologoId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Psicologo p = doc.toObject(Psicologo.class);
                        if (p != null) p.setId(doc.getId());
                        successListener.onSuccess(p);
                    }
                })
                .addOnFailureListener(failureListener);
    }

    public void editarConta(String psicologId,
                            String novoEmail,
                            String senhAtual,
                            String novaSenha,
                            OnSuccessListener<Void> successListener,
                            OnFailureListener failureListener){

        DocumentReference docPsicologo = db.collection("psicologo").document(psicologId);
        DocumentReference docUser = db.collection("user").document(psicologId);
        WriteBatch batch = db.batch();

        FirebaseUser user = auth.getCurrentUser();
        if(user == null || user.getEmail() == null){
            failureListener.onFailure(new Exception("Psicólogo não autenticado"));
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), senhAtual);

        user.reauthenticate(credential).addOnSuccessListener(aVoid -> {

            user.updatePassword(novaSenha).addOnSuccessListener(vPass -> {

                user.updateEmail(novoEmail).addOnSuccessListener(vEmail -> {

                    user.sendEmailVerification();

                    batch.update(docPsicologo, "email", novoEmail);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", novoEmail);
                    batch.set(docUser, userData, SetOptions.merge());

                    batch.commit()
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener);
                }).addOnFailureListener(e -> {
                    failureListener.onFailure(new Exception("Senha alterada com sucesso, mas erro ao mudar email: " + e.getMessage()));
                });
            }).addOnFailureListener(e -> {
                failureListener.onFailure(new Exception("Erro ao trocar de senha: " + e.getMessage()));
            });
        }).addOnFailureListener(e -> failureListener.onFailure(new Exception("Senha atual incorreta")));
    }

    public void excluirConta(String psicologId,
                             OnSuccessListener<Void> successListener,
                             OnFailureListener failureListener){

        WriteBatch batch = db.batch();
        DocumentReference docPsicologo = db.collection("psicologo").document(psicologId);
        DocumentReference docUser = db.collection("user").document(psicologId);

        batch.delete(docPsicologo);
        batch.delete(docUser);

        batch.commit().addOnSuccessListener(aVoid -> {
            if (auth.getCurrentUser() != null) {
                auth.getCurrentUser().delete();
            }
            successListener.onSuccess(aVoid);
        }).addOnFailureListener(failureListener);
    }
}