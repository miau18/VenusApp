package com.example.vnus.data.model;

public class Psicologo extends Users{

    private String crp;
    public Psicologo(){
    }

    public Psicologo(String id, String nome, String sobrenome, String email, String crp){
        super(id, nome, sobrenome, email, "Psicologo");
        this.crp=crp;
    }

    public String getCrp() { return crp; }
    public void setCrp(String crp) { this.crp=crp; }
}
