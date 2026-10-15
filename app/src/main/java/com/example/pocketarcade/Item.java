package com.example.pocketarcade;

// classe de modelo: cada item da lista do randomizador
public class Item {

    private final String palavra;
    private final String categoria;

    public Item(String palavra, String categoria) {
        this.palavra = palavra;
        this.categoria = categoria;
    }

    public String getPalavra() {
        return palavra;
    }

    public String getCategoria() {
        return categoria;
    }
}
