package com.groupoffive.listapp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    protected String nome;
    @OneToMany(mappedBy="categoria", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    protected Set<Produto> produtos = new HashSet<>();

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria(String nome, Set<Produto> produtos) {
        this.nome     = nome;
        this.produtos = produtos;
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

    public Set<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(Set<Produto> produtos) {
        this.produtos = produtos;
    }

}