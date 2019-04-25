package com.groupoffive.listapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "usuario_grupo")
public class UsuarioGrupo {

    @JsonIgnore
    @EmbeddedId
    private UsuarioGrupoPK id = new UsuarioGrupoPK();

    @Column
    private boolean admin;

    public UsuarioGrupo(Usuario user, GrupoDeUsuarios group, boolean admin) {
        group.getUsuarios().add(this);
        user.getGrupos().add(this);

        this.id = new UsuarioGrupoPK(user, group);
        this.admin = admin;
    }

    public UsuarioGrupo(GrupoDeUsuarios group, Usuario user, boolean admin) {
        this(user, group, admin);
    }

    public UsuarioGrupo() {

    }

    public Usuario getUsuario(){
        return this.id.getUser();
    }

    public void setUsuario(Usuario usuario){
        this.id.setUser(usuario);
    }

    @JsonIgnore
    public GrupoDeUsuarios getGrupo() {
        return this.id.getGroup();
    }

    @JsonProperty
    public void setGrupo(GrupoDeUsuarios grupo){
        this.id.setGroup(grupo);
    }

    public UsuarioGrupoPK getId() {
        return id;
    }

    public void setId(UsuarioGrupoPK id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
