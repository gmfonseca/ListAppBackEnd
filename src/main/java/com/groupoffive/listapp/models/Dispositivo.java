package com.groupoffive.listapp.models;

import javax.persistence.*;

@Entity
@Table(name = "dispositivo")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    protected String firebaseToken;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="Usuario_id")
    private Usuario usuario;

    public Dispositivo() {

    }

    public Dispositivo(String firebaseToken, Usuario usuario) {
        this.firebaseToken = firebaseToken;
        this.usuario       = usuario;
    }

    public int getId() {
        return id;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
