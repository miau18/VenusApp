package com.example.vnus.data.model;

import java.util.Date;
import java.util.List;

public class RedeApoio {

    private String id;

    //Relações
    private Usuaria criadora;
    private List<Users> membros;

    public RedeApoio(){
    }

    public RedeApoio(String id, Usuaria criadora, List<Users> membros){
        super();
        this.id = id;
        this.criadora = criadora;
        this.membros = membros;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public Usuaria getCriadoraId() { return criadora; }
    public void setCriadoraId(Usuaria criadora) { this.criadora = criadora; }
    public List<Users> getMembrosIds() { return membros; }
    public void setMembrosIds(List<Users> membrosIds) { this.membros = membros; }
}
