package com.groupoffive.listapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lista_de_compras")
public class ListaDeCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nome;
    @ManyToOne
    @JoinColumn(name="grupo_de_usuarios_id")
    private GrupoDeUsuarios grupoDeUsuarios;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "produto_lista",
            joinColumns = { @JoinColumn(name = "lista_de_compras_id") },
            inverseJoinColumns = { @JoinColumn(name = "produto_id") }
    )
    private Set<Produto> produtos = new HashSet<>();

    public ListaDeCompras() {}

    public ListaDeCompras(String name) {
        this.setNome(nome);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonIgnore
    public GrupoDeUsuarios getGrupoDeUsuarios() {
        return grupoDeUsuarios;
    }

    @JsonProperty
    public void setGrupoDeUsuarios(GrupoDeUsuarios grupoDeUsuarios) {
        this.grupoDeUsuarios = grupoDeUsuarios;
    }

    @JsonIgnore
    public Set<Produto> getProdutos() {
        return produtos;
    }

    @JsonProperty
    public void setProdutos(Set<Produto> produtos) {
        this.produtos = produtos;
    }
}