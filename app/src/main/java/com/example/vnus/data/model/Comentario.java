package com.example.vnus.data.model;

import java.util.Date;

public class Comentario {

    private String id;
    private String conteudo;
    private Date dataComentario;

    //Relações
    private Users usuarioAtual;
    private Relato relato;

    public Comentario(){
    }

    public Comentario(String id, String conteudo, Date dataComentario){
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.dataComentario = dataComentario;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo=conteudo; }
    public Date getDataComentario() { return dataComentario; }
    public void setDataComentario() { this.dataComentario=dataComentario; }
    public Users getUser() {return usuarioAtual;}
    public void setUser(Users usuarioAtual) {this.usuarioAtual = usuarioAtual;}
}
