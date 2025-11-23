package com.example.vnus.data.model;
public class Conversa {
    private String id;
    private String nomeContato;
    private String ultimaMensagem;
    private String hora;
    private String contatoId;

    public Conversa(){}
    public Conversa(String id, String nomeContato, String ultimaMensagem, String hora, String contatoId) {
        this.nomeContato = nomeContato;
        this.ultimaMensagem = ultimaMensagem;
        this.hora = hora;
        this.id=id;
        this.contatoId = contatoId;
    }

    public String getNomeContato() {
        return nomeContato;
    }
    public void setNomeContato(String nomeContato) {this.nomeContato = nomeContato;}
    public String getUltimaMensagem() {
        return ultimaMensagem;
    }
    public void setUltimaMensagem(String ultimaMensagem) {this.ultimaMensagem = ultimaMensagem;}
    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {this.hora = hora;}
    public String getId() { return id; }
    public void setId(String idConversa) { this.id = id; }
    public String getContatoId() { return contatoId; }
    public void setContatoId(String contatoId) { this.contatoId = contatoId; }
}
