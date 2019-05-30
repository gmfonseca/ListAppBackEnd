package com.groupoffive.listapp.models;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsuarioGrupoPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="grupo_de_usuarios_id", insertable = false, updatable = false)
    private GrupoDeUsuarios group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="usuario_id", insertable = false, updatable = false)
    private Usuario user;

    public UsuarioGrupoPK(Usuario user, GrupoDeUsuarios group) {
        this.group = group;
        this.user = user;
    }

    public UsuarioGrupoPK(GrupoDeUsuarios group, Usuario user){
        this(user, group);
    }

    public UsuarioGrupoPK() {
    }

    public GrupoDeUsuarios getGroup() {
        return group;
    }

    public void setGroup(GrupoDeUsuarios group) {
        this.group = group;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioGrupoPK that = (UsuarioGrupoPK) o;
        return Objects.equals(group, that.group) &&
               Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, user);
    }
}