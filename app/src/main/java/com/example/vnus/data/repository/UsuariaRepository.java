package com.example.vnus.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vnus.data.model.Usuaria;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class UsuariaRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void saveUsuariaSpecifiData(Usuaria usuaria,
                                       OnSuccessListener<Void> successListener,
                                       OnFailureListener failureListener) {
        db.collection("usuaria")
                .document(usuaria.getId())
                .set(usuaria)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getUsuariaPorId(String usuariaId,
                                OnSuccessListener<Usuaria> successListener,
                                OnFailureListener failureListener) {
        db.collection("usuaria")
                .document(usuariaId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Usuaria u = doc.toObject(Usuaria.class);
                        if (u != null) u.setId(doc.getId());
                        successListener.onSuccess(u);
                    }
                })
                .addOnFailureListener(failureListener);
    }

    public void editarCadastro(String usuariaId,
                               String novoEmailOuCodinome,
                               String senhaAtual,
                               String novaSenha,
                               boolean anonima,
                               OnSuccessListener<Void> successListener,
                               OnFailureListener failureListener) {

        DocumentReference docUsuaria = db.collection("usuaria").document(usuariaId);
        DocumentReference docUser = db.collection("user").document(usuariaId);
        WriteBatch batch = db.batch();

        if (anonima) {
            batch.update(docUsuaria, "codinome", novoEmailOuCodinome, "senhaAnonima", novaSenha);
            batch.update(docUser, "nome", novoEmailOuCodinome);
            batch.commit()
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener);

        } else {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null || user.getEmail() == null) {
                failureListener.onFailure(new Exception("Usuária não autenticada."));
                return;
            }
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), senhaAtual);

            user.reauthenticate(credential).addOnSuccessListener(aVoid -> {

                user.updatePassword(novaSenha).addOnSuccessListener(vPass -> {

                    user.updateEmail(novoEmailOuCodinome).addOnSuccessListener(vEmail -> {

                        user.sendEmailVerification();

                        batch.update(docUsuaria, "email", novoEmailOuCodinome);
                        batch.update(docUser, "email", novoEmailOuCodinome);

                        batch.commit()
                                .addOnSuccessListener(successListener)
                                .addOnFailureListener(failureListener);

                    }).addOnFailureListener(e -> {
                        failureListener.onFailure(new Exception("Senha alterada com sucesso, mas erro ao mudar e-mail: " + e.getMessage()));
                    });

                }).addOnFailureListener(e -> {
                    failureListener.onFailure(new Exception("Erro ao trocar senha: " + e.getMessage()));
                });

            }).addOnFailureListener(e -> failureListener.onFailure(new Exception("Senha atual incorreta.")));
        }
    }

    public void excluirConta(String usuariaId, boolean anonima,
                             OnSuccessListener<Void> successListener,
                             OnFailureListener failureListener) {

        WriteBatch batch = db.batch();
        DocumentReference docUsuaria = db.collection("usuaria").document(usuariaId);
        DocumentReference docUser = db.collection("user").document(usuariaId);

        batch.delete(docUsuaria);
        batch.delete(docUser);

        batch.commit().addOnSuccessListener(aVoid -> {
            if (!anonima && auth.getCurrentUser() != null) {
                auth.getCurrentUser().delete();
            }
            successListener.onSuccess(aVoid);
        }).addOnFailureListener(failureListener);
    }

    public String getUsuariaLogadaId(Context c) {
        SharedPreferences sp = c.getSharedPreferences("DadosUsuario", Context.MODE_PRIVATE);
        String idBanco = sp.getString("id_banco_real", null);

        if (idBanco != null) {
            return idBanco;
        }
        else if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }

        return null;
    }

    public void logoutUsuaria(Context c, boolean anonima) {
        if (!anonima) {
            FirebaseAuth.getInstance().signOut();
        }
        c.getSharedPreferences("DadosUsuario", Context.MODE_PRIVATE).edit().clear().apply();
    }
}