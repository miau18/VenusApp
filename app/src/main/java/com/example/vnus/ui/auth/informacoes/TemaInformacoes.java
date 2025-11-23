package com.example.vnus.ui.auth.informacoes;

public class TemaInformacoes {
    private String titulo;
    private String descricao;
    private int imagem;

    public TemaInformacoes(String titulo, String descricao, int imagem) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public int getImagem() { return imagem; }
}