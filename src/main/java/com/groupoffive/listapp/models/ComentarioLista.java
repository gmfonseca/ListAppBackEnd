package com.groupoffive.listapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "comentario_lista")
public class ComentarioLista implements Comparable<ComentarioLista> {

    @EmbeddedId
    private ComentarioListaPK id;

    public ComentarioLista(Usuario user, ListaDeCompras list, Comentario comment) {
        this.id = new ComentarioListaPK(user, list, comment);
    }

    public ComentarioLista(ListaDeCompras list, Usuario user, Comentario comment) {
        this.id = new ComentarioListaPK(user, list, comment);
    }

    public ComentarioLista(){

    }

    @JsonIgnore
    public ListaDeCompras getList() {
        return id.getList();
    }

    @JsonProperty
    public void setList(ListaDeCompras list) {
        id.setList(list);
    }

    public Usuario getUser() {
        return id.getUser();
    }

    public void setUser(Usuario user) {
        id.setUser(user);
    }

    public Comentario getComment() {
        return id.getComment();
    }

    public void setComment(Comentario comment) {
        id.setComment(comment);
    }

    @Override
    public int compareTo(ComentarioLista o) {
        return new Integer(this.getComment().getId()).compareTo(o.getComment().getId());
    }
}
