package com.groupoffive.listapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String comentario;

    @OneToMany(mappedBy = "id.comment", fetch = FetchType.EAGER)
    private Set<ComentarioLista> listas = new HashSet<>();

    public Comentario(String comentario) {
        this.comentario = comentario;
    }

    public Comentario() {}

    public int getId() {
        return id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @JsonIgnore
    public Set<ComentarioLista> getListas() {
        return listas;
    }

    @JsonProperty
    public void setListas(Set<ComentarioLista> listas) {
        this.listas = listas;
    }
}
