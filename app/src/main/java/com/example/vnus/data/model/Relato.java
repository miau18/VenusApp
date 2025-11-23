package com.example.vnus.data.model;

import java.util.Date;
import java.util.List;

public class Relato {

    private String id;
    private String conteudo;
    private boolean anonimo;
    private Date dataPublicacao;
    private int curtidas;

    //Relações
    private Usuaria usuaria;
    private List<Users> curtidoPor;
    private List<Comentario> comentarios;
    private Hospital hospital;
    private String idUsuaria;

    public Relato(){
    }

    public Relato(String id, String conteudo, boolean anonimo, Date dataPublicacao, int curtidas){
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.anonimo = anonimo;
        this.dataPublicacao = dataPublicacao;
        this.curtidas = curtidas;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo=conteudo; }
    public boolean getAnonimo() { return anonimo; }
    public void setAnonimo(boolean anonimo) { this.anonimo = anonimo; }
    public Date getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
    public int getCurtidas() { return curtidas; }
    public void setCurtidas(int curtidas) { this.curtidas=curtidas; }
    public Usuaria getUsuaria() {return usuaria;}
    public void setUsuaria(Usuaria usuaria) {this.usuaria=usuaria;}
    public List<Users> getCurtidoPor() {return curtidoPor;}
    public void setCurtidoPor(List<Users> curtidoPor) {this.curtidoPor=curtidoPor;}
    public String getIdUsuaria() {return idUsuaria;}
    public void setIdUsuaria(String idUsuaria) {this.idUsuaria = idUsuaria;}
    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }
}
