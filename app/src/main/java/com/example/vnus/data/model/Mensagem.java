package com.example.vnus.data.model;

import com.google.type.DateTime;

public class Mensagem {

    private int id;
    private String conteudo;
    private DateTime horaEnvio;
    private DateTime dataAtualização;

    //Relações
    private Users remetente;
    private Users destinatario;
    private Chat chat;

    public Mensagem(){
    }

    public Mensagem(int id, String conteudo, DateTime horaEnvio, DateTime dataAtualização){
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.dataAtualização = dataAtualização;
        this.horaEnvio = horaEnvio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id=id; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo=conteudo; }

    public DateTime getHoraEnvio() { return horaEnvio; }
    public void setHoraEnvio(DateTime horaEnvio) { this.horaEnvio=horaEnvio; }

    public DateTime getDataAtualização() { return dataAtualização; }
    public void setDataAtualização(DateTime dataAtualização) { this.dataAtualização=dataAtualização; }
}
