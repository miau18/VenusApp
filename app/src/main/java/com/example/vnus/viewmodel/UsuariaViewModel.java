package com.example.vnus.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vnus.data.model.Usuaria;
import com.example.vnus.data.repository.UsuariaRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class UsuariaViewModel extends ViewModel {

    private final UsuariaRepository repository = new UsuariaRepository();
    private final MutableLiveData<Boolean> cadastroSucesso = new MutableLiveData<>();
    private final MutableLiveData<String> erro = new MutableLiveData<>();
    public LiveData<Boolean> getCadastroSucesso() {
        return cadastroSucesso;
    }
    public LiveData<String> getErro() {
        return erro;
    }
    public void salvarUsuaria(Usuaria usuaria) {
        repository.saveUsuariaSpecifiData(
                usuaria,
                aVoid -> cadastroSucesso.setValue(true),
                e -> erro.setValue(e.getMessage())
        );
    }
}
