package com.example.vnus.data.model;

import java.util.List;

public class Usuaria extends Users{

    private boolean anonima;
    private String codinome;
    private String senhaAnonima;

    //Relações
    private List<Relato> relatos;
    private RedeApoio redeDeApoio;

    public Usuaria(){
    }

    public Usuaria(String id, String nome, String sobrenome, String email){
        super(id, nome, sobrenome, email, "Usuária");
        this.anonima = false;
    }

    public Usuaria(String id, String codinome, String senhaAnonima) {
        super(id, null, null, null, "Usuária");
        this.anonima = true;
        this.codinome = codinome;
        this.senhaAnonima = senhaAnonima;
    }

    public boolean isAnonima() { return anonima; }
    public void setAnonima(boolean anonima) { this.anonima = anonima; }

    public String getCodinome() { return codinome; }
    public void setCodinome(String codinome) { this.codinome = codinome; }

    public String getSenhaAnonima() { return senhaAnonima; }
    public void setSenhaAnonima(String senhaAnonima) { this.senhaAnonima = senhaAnonima; }
}
