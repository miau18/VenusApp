package com.example.vnus.data.model;

import java.util.Date;

public class Mensagem {

    private String id;
    private String conteudo;
    private Date horaEnvio;
    private Date dataAtualizacao;

    // Relações
    private Users remetente;
    private Users destinatario;

    public Mensagem(){
    }

    public Mensagem(String id, String conteudo, Date horaEnvio, Date dataAtualizacao){
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.dataAtualizacao = dataAtualizacao;
        this.horaEnvio = horaEnvio;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo=conteudo; }
    public Date getHoraEnvio() { return horaEnvio; }
    public void setHoraEnvio(Date horaEnvio) { this.horaEnvio=horaEnvio; }
    public Date getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Date dataAtualizacao) { this.dataAtualizacao=dataAtualizacao; }
    public Users getRemetente() { return remetente; }
    public void setRemetente(Users remetente) { this.remetente = remetente; }
    public Users getDestinatario() { return destinatario; }
    public void setDestinatario(Users destinatario) { this.destinatario = destinatario; }
}