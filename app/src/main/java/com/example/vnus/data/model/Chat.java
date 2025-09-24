package com.example.vnus.data.model;

import java.util.List;

public class Chat {

    private int id;

    //Relações
    private List<Mensagem> mensagens;
    private Users participante;

    public Chat(){
    }

    public Chat(int id){
        super();
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id=id; }
}
