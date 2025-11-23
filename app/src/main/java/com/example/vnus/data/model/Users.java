package com.example.vnus.data.model;

import java.util.List;

public class Users {

    protected String id;
    protected String email;
    protected String nome;
    protected String sobrenome;
    protected String tipoUser;

    //Relações
    protected List<Relato> relatosCompartilhados;
    protected List<Users> relatosCurtidos;
    protected List<Comentario> comentariosFeitos;
    protected List<Mensagem> mensagensEnviadas;
    protected List<Mensagem> mensagensRecebidas;

    public Users(){
    }

    public Users(String id, String nome, String sobrenome, String email, String tipoUser){
        this.id=id;
        this.nome=nome;
        this.sobrenome=sobrenome;
        this.email=email;
        this.tipoUser=tipoUser;
    }

    public String getId() { return this.id; }
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
