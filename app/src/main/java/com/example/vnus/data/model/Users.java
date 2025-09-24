package com.example.vnus.data.model;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class Users {

    private String id;
    private String email;
    private String nome;
    private String sobrenome;
    private String tipoUser;

    //Relações
    private List<Relato> relatosCompartilhados;
    private List<Users> relatosCurtidos;
    private List<Comentario> comentariosFeitos;
    private List<Mensagem> mensagensEnviadas;
    private List<Mensagem> mensagensRecebidas;
    private List<Chat> chatsQueParticipa;

    public Users(){
    }

    public Users(String id, String nome, String sobrenome, String email, String tipoUser){
        this.id=id;
        this.nome=nome;
        this.sobrenome=sobrenome;
        this.email=email;
        this.tipoUser=tipoUser;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id=id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome=nome; }
    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTipoUser() { return tipoUser; }
    public void setTipoUser(String tipoUser) { this.tipoUser=tipoUser; }
}
