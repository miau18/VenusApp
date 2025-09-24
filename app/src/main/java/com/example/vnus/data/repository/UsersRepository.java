package com.example.vnus.data.repository;

import com.example.vnus.data.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveUser(Users user,
                         OnSuccessListener<Void> successListener,
                         OnFailureListener failureListener) {
        db.collection("user")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
