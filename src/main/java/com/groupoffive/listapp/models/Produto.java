package com.groupoffive.listapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "produto")
public class Produto /*implements Comparable<Produto>*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nome;
    private double preco;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="categoria_id")
    private Categoria categoria;

    public Produto() {}

    public Produto(String nome, double preco) {
        this.nome  = nome;
        this.preco = preco;
    }

    public Produto(String nome, double preco, Categoria categoria) {
        this.nome      = nome;
        this.preco     = preco;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @JsonIgnore
    public Categoria getCategoria() {
        return categoria;
    }

    @JsonIgnore
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean belongsToList(ListaDeCompras lista, EntityManager entityManager) {
        try {
            entityManager.createQuery(
                    "SELECT p " +
                            "FROM Produto p " +
                            "INNER JOIN ProdutoLista pl ON pl.produto = p " +
                            "INNER JOIN pl.listaDeCompras l " +
                            "WHERE p.id = :idProduto " +
                            "AND l.id = :idLista"
            ).setParameter("idProduto", this.getId())
            .setParameter("idLista", lista.getId())
            .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
//
//    @Override
//    public int compareTo(Produto o) {
//        return o.id == this.id ? 0 : -1;
//    }
}