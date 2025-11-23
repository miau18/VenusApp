package com.example.vnus.data.model;

public class Psicologo extends Users{

    private String crp;
    private String id;
    public Psicologo(){
    }

    public Psicologo(String id, String nome, String sobrenome, String email, String crp){
        super(id, nome, sobrenome, email, "Psicologo");
        this.crp=crp;
        this.id = id;
    }

    public String getCrp() { return crp; }
    public void setCrp(String crp) { this.crp=crp; }
    public String getId() { return id; }
    public void setId(String id) {this.id = id;}

}
