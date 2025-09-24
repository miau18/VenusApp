package com.example.vnus.data.model;

import java.util.Date;
import java.util.List;

public class RedeApoio {

    private int id;

    //Relações
    private Usuaria criadora;
    private List<Users> membros;

    public RedeApoio(){
    }

    public RedeApoio(int id){
        super();
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id=id; }
}
