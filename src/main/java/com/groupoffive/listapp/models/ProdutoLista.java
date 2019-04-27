package com.groupoffive.listapp.models;

import javax.persistence.*;

@Entity
@Table(name = "produto_lista")
public class ProdutoLista {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name="lista_de_compras_id")
    private ListaDeCompras listaDeCompras;

    @ManyToOne
    @JoinColumn(name="produto_id")
    private Produto produto;

    public ProdutoLista() {

    }

    public ProdutoLista(ListaDeCompras listaDeCompras, Produto produto) {
        this.listaDeCompras = listaDeCompras;
        this.produto = produto;
    }

    public ListaDeCompras getListaDeCompras() {
        return listaDeCompras;
    }

    public Produto getProduto() {
        return produto;
    }
}