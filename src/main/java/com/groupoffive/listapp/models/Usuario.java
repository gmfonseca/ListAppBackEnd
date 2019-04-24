package com.groupoffive.listapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull
    private String nome;

    @NotNull
    private String email;

    @NotNull
    private String senha;

    @OneToMany(mappedBy = "id.user")
    private Set<Comentario> comentarios = new HashSet<>();

    @OneToMany(mappedBy = "id.user")
    private Set<UsuarioGrupo> grupos = new HashSet<>();

    public Usuario() {}

    public Usuario(String nome, String email, String senha) {
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public Set<UsuarioGrupo> getGrupos() {
        return grupos;
    }

    @JsonProperty
    public void setGrupos(Set<UsuarioGrupo> grupos) {
        this.grupos = grupos;
    }

    public void addComentario(Comentario comentario){
        comentarios.add(comentario);
    }

    @JsonIgnore
    public Set<Comentario> getComentarios() {
        return comentarios;
    }

    @JsonProperty
    public void setComentarios(Set<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id &&
                nome.equals(usuario.nome) &&
                email.equals(usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, email);
    }
}