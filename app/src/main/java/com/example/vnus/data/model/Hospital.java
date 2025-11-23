package com.example.vnus.data.model;

import java.util.Date;
import java.util.List;

public class Hospital {

    private String id;
    private String nome;
    private String latitude;
    private String longitude;

    //Relações
    private List<Relato> relatosQueFoiCitado;

    public Hospital(){
    }

    public Hospital(String id, String nome, String latitude, String longitude){
        super();
        this.id = id;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome=nome; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude=latitude; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String latitude) { this.latitude=latitude; }

    // Faz o Spinner exibir só o nom
    @Override
    public String toString() {
        return nome;
    }
}
