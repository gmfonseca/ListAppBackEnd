package com.groupoffive.listapp.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ComentarioListaPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="comentario_id", insertable = false, updatable = false)
    private Comentario comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="lista_compra_id", insertable = false, updatable = false)
    private ListaDeCompras list;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="usuario_id", insertable = false, updatable = false)
    private Usuario user;

    public ComentarioListaPK(Usuario user, ListaDeCompras list, Comentario comment) {
        this.comment = comment;
        this.list = list;
        this.user = user;
    }

    public ComentarioListaPK(ListaDeCompras list, Usuario user, Comentario comment){
        this(user, list, comment);
    }

    public ComentarioListaPK() {
    }

    public ListaDeCompras getList() {
        return list;
    }

    public void setList(ListaDeCompras list) {
        this.list = list;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public Comentario getComment() {
        return comment;
    }

    public void setComment(Comentario comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComentarioListaPK that = (ComentarioListaPK) o;
        return Objects.equals(comment, that.comment) &&
                Objects.equals(list, that.list) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, list, user);
    }
}