package com.example.vnus.data.model;

import java.util.Date;

public class Comentario {

    private int id;
    private String conteudo;
    private Date dataComentario;

    //Relações
    private Users user;
    private Relato relato;

    public Comentario(){
    }

    public Comentario(int id, String conteudo, Date dataComentario){
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.dataComentario = dataComentario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id=id; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo=conteudo; }

    public Date getDataComentario() { return dataComentario; }
    public void setDataComentario() { this.dataComentario=dataComentario; }

    public Users getUser() {return user;}

    public void setUser() {this.user = user;}
}
